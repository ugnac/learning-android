package com.learn.kotlin

import kotlinx.coroutines.*
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis


/**
 * 共享的可变状态值与并发
 * 使用多线程的派发器, 比如 Dispatchers.Default, 协程可以并发执行. 因此协程也面对并发带来的所有问题. 主要问题是访问 共享的可变状
 * 态值 时的同步问题. 在协程的世界里, 这类问题的有些解决方案与在线程世界中很类似, 但另外一些方案就非常不同.
 */

/**
 * 问题的产生
 * 下面我们启动 100 个协程, 每个协程都将同样的操作执行 1000 次. 我们测量一下它们的结束时间, 并做进一步的比较:
 */

private suspend fun CoroutineScope.massiveRun(action: suspend () -> Unit) {
    val n = 100 // 启动的协程数量
    val k = 1000 // 每个协程执行操作的重复次数
    val time = measureTimeMillis {
        val jobs = List(n) {
            launch {
                repeat(k) { action() }
            }
        }
        jobs.forEach { it.join() }
    }
    println("Completed ${n * k} actions in $time ms")
}

private var counter = 0

/**
 * 这段代码的最终输出结果是什么? 很大概率它不会输出 “Counter = 100000”, 因为 100 个协程运行在多线程环境内, 它们同时给
 * counter 加 1, 但没有任何的同步机制.
 */
private fun testCoroutines01() = runBlocking {
    GlobalScope.massiveRun {
        counter++
    }
    println("Counter = $counter")
}

private val mtContext = newFixedThreadPoolContext(2, "mtPool") // 明确定义上下文, 使用 2 个线程
private var counter2 = 0

private fun testCoroutines02() = runBlocking {
    CoroutineScope(mtContext).massiveRun { // 使用自定义的上下文, 而不是 Dispatchers.Default
        counter2++
    }
    println("Counter2 = $counter2")
}

@Volatile // 在 Kotlin 中, `volatile` 是注解
private var counter3 = 0

/**
 * volatile 不能解决这个问题
 * 有一种常见的错误观念, 认为把变量变为 volatile 就可以解决并发访问问题. 我们来试一下:
 *
 * 代码运行变慢了, 但最终我们还是得不到 “Counter = 100000”, 因为 volatile 变量保证线性的(linearizable) (意思就是 “原子性
 * (atomic)”) 读和写操作, 但不能保证更大的操作(在我们的例子中, 就是加 1 操作)的原子性.
 */
private fun testCoroutines03() = runBlocking {
    GlobalScope.massiveRun {
        counter3++
    }
    println("Counter3 = $counter3")
}

/**
 * 线程安全的数据结构
 * 一种对于线程和协程都能够适用的解决方案是, 使用线程安全的 (也叫 同步的(synchronized), 线性的(linearizable), 或者 原子化的
 * (atomic)) 数据结构, 这些数据结构会对需要在共享的状态数据上进行的操作提供需要的同步保障. 在我们的简单的计数器示例中, 可以使
 * 用 AtomicInteger 类, 它有一个原子化的 incrementAndGet 操作:
 *
 * 对于这个具体的问题, 这是最快的解决方案. 这种方案适用于计数器, 集合, 队列, 以及其他标准数据结构, 以及这些数据结构的基本操作.
 * 但是, 这种方案并不能简单地应用于复杂的状态变量, 或者那些没有现成的线程安全实现的复杂操作.
 */
private var counter4 = AtomicInteger()
private fun testCoroutines04() = runBlocking {
    GlobalScope.massiveRun {
        counter4.incrementAndGet()
    }
    println("Counter4 = $counter4")
}

/**
 * 细粒度的线程限定
 *
 * 线程限定(Thread confinement) 是共享的可变状态值问题的一种解决方案, 它把所有对某个共享值的访问操作都限定在唯一的一个线程
 * 内. 最典型的应用场景是 UI 应用程序, 所有的 UI 状态都被限定在唯一一个 事件派发(event-dispatch) 线程 或者叫 application 线程内. 通
 * 过使用单线程的上下文, 可以很容易地对协程使用这种方案
 */

private val counterContext = newSingleThreadContext("CounterContext")
private var counter5 = 0
/**
 * 这段代码的运行速度会非常地慢, 因为它进行了 细粒度
 * (fine-grained) 的线程限定. 每一次加 1 操作都必须使用 withContext, 从多线程的
 * Dispatchers.Default 上下文切换到单一线程上下文.
 */
private fun testCoroutines05() = runBlocking {
    GlobalScope.massiveRun { // 使用 DefaultDispathcer 运行每个协程
        withContext(counterContext) { // 但把所有的加 1 操作都限定在单一线程的上下文中
            counter5++
        }
    }
    println("Counter5 = $counter5")
}

/**
 * 粗粒度的线程限定
 * 在实际应用中, 通常在更大的尺度上进行线程限定, 比如, 将大块的状态更新业务逻辑限定在单个线程中. 下面的示例程序就是这样做的,
 * 它在单一线程的上下文中运行每个协程. 这里我们使用 CoroutineScope() 函数来将协程的上下文转换为 CoroutineScope 类型:
 */
private fun testCoroutines06() = runBlocking {
    counter5 = 0
    CoroutineScope(counterContext).massiveRun { // 在单一线程的上下文中运行每个协程
        counter5++
    }
    println("Counter6 = $counter5")
}


private fun testCoroutines07() = runBlocking {
}

private fun testCoroutines08() = runBlocking {

}


private fun testCoroutines09() = runBlocking {

}

private fun testCoroutines10() = runBlocking<Unit> {
}

fun main() {
    testCoroutines01()
    testCoroutines02()
    testCoroutines03()
    testCoroutines04()
    testCoroutines05()
    testCoroutines06()
//    testCoroutines07()
//    testCoroutines08()
//    testCoroutines09()
//    testCoroutines10()
}