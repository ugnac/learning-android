package com.learn.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.ticker

/**
 * 通道(Channel) (实验性功能)
 * 通道的基本概念
 * Channel 在概念上非常类似于 BlockingQueue . 关键的不同是, 它没有阻塞的 put 操作, 而是提供挂起的 send 操作, 没有阻塞的 take
 * 操作, 而是提供挂起的 receive 操作
 */
private fun testCoroutines01() = runBlocking {
    val channel = Channel<Int>()
    launch {
        // 这里可能是非常消耗 CPU 的计算工作, 或者是一段异步逻辑, 但在这个例子中我们只是简单地发送 5 个平方数
        for (x in 1..20) channel.send(x * x)
    }
    // 我们在这里打印收到的整数:
    repeat(20) { println(channel.receive()) }
    println("Done!")
}

/**
 * 通道的关闭与迭代
 * 与序列不同, 通道可以关闭, 表示不会再有更多数据从通道传来了. 在通道的接收端可以使用 for 循环很方便地从通道中接收数据.
 * 概念上来说, close 操作类似于向通道发送一个特殊的关闭标记. 收到这个关闭标记之后, 对通道的迭代操作将会立即停止, 因此可以保证
 * 在关闭操作以前发送的所有数据都会被正确接收:
 */
private fun testCoroutines02() = runBlocking {
    val channel = Channel<Int>()
    launch {
        // 这里可能是非常消耗 CPU 的计算工作, 或者是一段异步逻辑, 但在这个例子中我们只是简单地发送 5 个平方数
        for (x in 1..20) channel.send(x * x)
        channel.close() // 我们已经发送完了所有的数据
    }

    // 我们在这里使用 `for` 循环来打印接收到的数据 (通道被关闭后循环就会结束)
    for (y in channel) Log.d("testCoroutines02: ", y)
    println("Done!")
}

/**
 * 构建通道的生产者(Producer)
 * 在协程中产生一个数值序列, 这是很常见的模式. 这是并发代码中经常出现的 生产者(producer)/消费者(consumer) 模式的一部分.
 * 你可以将生产者抽象为一个函数, 并将通道作为函数的参数, 然后向通道发送你生产出来的值, 但这就违反了通常的函数设计原则,
 * 也就是函数的结果应该以返回值的形式对外提供.
 *
 * 有一个便利的协程构建器, 名为 produce, 它可以很简单地编写出生产者端的正确代码, 还有一个扩展函数 consumeEach, 可以在消费者
 * 端代码中替代 for 循环:
 */
private fun testCoroutines03() = runBlocking {
    val squares = produceSquares()
    squares.consumeEach { println(it) }
    println("Done!")
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}

/**
 * 管道（Pipeline）
 * 管道也是一种设计模式, 比如某个协程可能会产生出无限多个值:
 */
private fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) send(x++) // 从 1 开始递增的无限整数流
}

/**
 * 其他的协程(或者多个协程)可以消费这个整数流, 进行一些处理, 然后产生出其他结果值.
 * 下面的例子中, 我们只对收到的数字做平方运算:
 */
private fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
    for (x in numbers) send(x * x)
}

/**
 * 主代码会启动这些协程, 并将整个管道连接在一起:
 * 所有创建协程的函数都被定义为 CoroutineScope 上的扩展函数, 因此我们可以依靠 结构化的并发 来保证应用程序中没有留下长期
 * 持续的全局协程.
 */
private fun testCoroutines04() = runBlocking {
    val numbers = produceNumbers() // 从 1 开始产生无限的整数
    val squares = square(numbers) // 对整数进行平方
    for (i in 1..5) println(squares.receive()) // 打印前 5 个数字
    println("Done!") // 运行结束
    coroutineContext.cancelChildren() // 取消所有的子协程
}

/**
 * 使用管道寻找质数
 * 下面我们来编写一个示例程序, 使用协程的管道来生成质数, 来演示一下管道的极端用法.
 * 首先我们产生无限的整数序列.
 */
private fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
    var x = start
    while (true) send(x++) // 从 start 开始递增的无限整数流
}

/**
 * 管道的下一部分会对输入的整数流进行过滤, 删除可以被某个质数整除的数字:
 */
private fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
    for (x in numbers) if (x % prime != 0) send(x)
}

/**
 * 注意, 你可以使用标准库的协程构建器 buildIterator 来创建相同的管道. 把 produce 函数替换为 buildIterator , 把 send 函数替换为
 * yield , 把 receive 函数替换为 next , 把 ReceiveChannel 替换为 Iterator , 就可以不用关心删除协程的作用范围了. 而且你也可以不再
 * 需要 runBlocking . 但是, 上面的示例中演示的, 使用通道的管道的好处在于, 如果你在 Dispatchers.Default 上下文中运行的话, 它可以使
 * 用 CPU 的多个核心.
 * 总之, 这是一个极不实用的寻找质数的方法. 在实际应用中, 管道一般会牵涉到一些其他的挂起函数调用(比如异步调用远程服务), 而且这
 * 些管道不能使用 buildSequence / buildIterator 来构建, 因为这些函数不能允许任意的挂起, 而不象 produce 函数, 是完全异步的
 */
private fun testCoroutines05() = runBlocking {
    var cur = numbersFrom(2)
    for (i in 1..10) {
        val prime = cur.receive()
        Log.d("testCoroutines05: ", prime)
        cur = filter(cur, prime)
    }
    coroutineContext.cancelChildren() // 取消所有的子协程, 让 main 函数结束
}

/**
 * 扇出(Fan-out)
 * 多个协程可能会从同一个通道接收数据, 并将计算工作分配给这多个协程. 我们首先来创建一个生产者协程, 它定时产生整数(每秒 10 个整
 * 数):
 */
private fun CoroutineScope.produceNumbers2() = produce<Int> {
    var x = 1 // 从 1 开始
    while (true) {
        send(x++) // 产生下一个整数
        delay(100) // 等待 0.1 秒
    }
}

/**
 * 然后我们创建多个数据处理协程. 这个示例程序中, 这些协程只是简单地打印自己的 id 以及接收到的整数:
 */
private fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
    for (msg in channel) {
        println("Processor #$id received $msg")
    }
}

/**
 * 现在我们启动 5 个数据处理协程, 让它们运行大约 1 秒. 看看结果如何:
 *
 * 注意, 取消生产者协程会关闭它的通道, 因此最终会结束各个数据处理协程中对这个通道的迭代循环. 而且请注意, 在 launchProcessor
 * 中, 我们是如何使用 for 循环明确地在通道上进行迭代, 来实现扇出(fan-out). 与 consumeEach 不同, 这个 for 循环模式完全可以安全
 * 地用在多个协程中. 如果某个数据处理协程失败, 其他数据处理协程还会继续处理通道中的数据, 而使用 consumeEach 编写的数据处理
 * 协程, 无论正常结束还是异常结束, 总是会消费(取消) 它的通道.
 */
private fun testCoroutines06() = runBlocking {
    val producer = produceNumbers2()
    repeat(5) { launchProcessor(it, producer) }
    delay(950)
    producer.cancel() // 取消生产者协程, 因此也杀死了所有其他数据处理协程
}

/**
 * 扇入(Fan-in)
 * 多个协程也可以向同一个通道发送数据. 比如, 我们有一个字符串的通道, 还有一个挂起函数, 不断向通道发送特定的字符串, 然后暂停一
 * 段时间:
 */
private suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
    while (true) {
        delay(time)
        channel.send(s)
    }
}

/**
 * 现在, 我们启动多个发送字符串的协程, 来看看结果如何(在这个示例程序中我们在主线程的上下文中启动这些协程, 作为主协程的子协程):
 */
private fun testCoroutines07() = runBlocking {
    val channel = Channel<String>()
    launch {
        sendString(channel, "foo", 200L)
    }
    launch {
        sendString(channel, "BAR!", 500L)
    }
    repeat(6) { // 接收前 6 个字符串
        println(channel.receive())
    }
    Log.d("testCoroutines07", "Main end")
    coroutineContext.cancelChildren() // 取消所有的子协程, 让 main 函数结束
}

/**
 * 带缓冲区的通道
 *
 */
private fun testCoroutines08() = runBlocking {
    val channel = Channel<Int>(4) // 创建带缓冲区的通道
    val sender = launch { // 启动发送者协程
        repeat(10) {
            println("Sending $it") // 发送数据之前, 先打印它
            channel.send(it) // 当缓冲区满时, 会挂起
        }
    }

    // 不接收任何数据, 只是等待
    delay(1000)
    sender.cancel() // 取消发送者协程
}

/**
 * 通道是平等的
 * 如果从多个协程中调用通道的发送和接收操作, 从调用发生的顺序来看, 这些操作是 平等的. 通道对这些方法以先进先出(first-in first-out)
 * 的顺序进行服务, 也就是说, 第一个调用 receive 的协程会得到通道中的数据. 在下面的示例程序中, 有两个 “ping” 和 “pong” 协程,
 * 从公用的一个 “table” 通道接收 “ball” 对象.
 */
private data class Ball(var hits: Int)

private fun testCoroutines09() = runBlocking {
    val table = Channel<Ball>() // 一个公用的通道
    launch {
        player("ping", table)
    }
    launch {
        player("pong", table)
    }
    table.send(Ball(0)) // 把 ball 丢进通道
    delay(1000) // 延迟 1 秒
    coroutineContext.cancelChildren() // 游戏结束, 取消所有的协程
}

private suspend fun player(name: String, table: Channel<Ball>) {
    for (ball in table) { // 使用 for 循环不断地接收 ball
        ball.hits++
        println("$name $ball")
        delay(300) // 延迟一段时间
        table.send(ball) // 把 ball 送回通道内
    }
}

/**
 * 定时器(Ticker)通道
 * 定时器(Ticker)通道是一种特别的会合通道(rendezvous channel), 每次通道中的数据耗尽之后, 它会延迟一个固定的时间, 并产生一个
 * Unit . 虽然它单独看起来好像毫无用处, 但它是一种很有用的零件, 可以创建复杂的基于时间的 produce 管道, 以及操作器, 执行窗口操作
 * 和其他依赖于时间的处理. 定时器通道可以用在 select 中, 执行 “on tick” 动作.
 *
 * 可以使用 ticker 工厂函数来创建这种通道. 使用通道的 ReceiveChannel.cancel 方法来指出不再需要它继续产生数据了.
 */
@OptIn(ObsoleteCoroutinesApi::class)
private fun testCoroutines10() = runBlocking<Unit> {
    val tickerChannel = ticker(delayMillis = 100, initialDelayMillis = 0) // 创建定时器通道
    var nextElement = withTimeoutOrNull(1) {
        tickerChannel.receive()
    }
    println("Initial element is available immediately: $nextElement") // 初始指定的延迟时间还未过去

    nextElement = withTimeoutOrNull(50) {
        tickerChannel.receive()
    }
    // 之后产生的所有数据的延迟时间都是 100ms
    println("Next element is not ready in 50 ms: $nextElement")


    nextElement = withTimeoutOrNull(60) {
        tickerChannel.receive()
    }
    println("Next element is ready in 100 ms: $nextElement")

    // 模拟消费者端的长时间延迟
    println("Consumer pauses for 150ms")
    delay(150)

    // 下一个元素已经产生了
    nextElement = withTimeoutOrNull(1) {
        tickerChannel.receive()
    }
    println("Next element is available immediately after large consumer delay: $nextElement")

    // 注意, `receive` 调用之间的暂停也会被计算在内,因此下一个元素产生得更快
    nextElement = withTimeoutOrNull(60) {
        tickerChannel.receive()
    }
    println("Next element is ready in 50ms after consumer pause in 150ms: $nextElement")
    tickerChannel.cancel() // 告诉通道, 不需要再产生更多元素了
}

fun main() {
//    testCoroutines01()
//    testCoroutines02()
//    testCoroutines03()
//    testCoroutines04()
//    testCoroutines05()
//    testCoroutines06()
//    testCoroutines07()
//    testCoroutines08()
//    testCoroutines09()
    testCoroutines10()
}