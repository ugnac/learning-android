package com.learn.kotlin

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 挂起函数(Suspending Function)的组合
 *
 * 默认的连续执行
 */
private suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // 假设我们在这里做了某些有用的工作
    return 13
}

private suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // 假设我们在这里也做了某些有用的工作
    return 29
}

private fun testCoroutines01() = runBlocking {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        Log.d("testCoroutines01", "The answer is ${one + two}")
    }
    Log.d("testCoroutines01", "Completed in $time ms")
}

/**
 * 挂起函数(Suspending Function)的组合
 *
 * 使用 async 并发执行
 * 概念上来说, async 就好象 launch 一样. 它启动一个独立的协程, 也就是一个轻量的线程, 与其他所有协程一起并发执行. 区别在于,
 * launch 返回一个 Job, 其中不带有结果值, 而 async 返回一个 Deferred – 一个轻量的, 非阻塞的 future, 代表一个未来某个时刻可以得
 * 到的结果值. 你可以对一个延期值(deferred value)使用 .await() 来得到它最终的计算结果, 但 Deferred 同时也是一个 Job , 因此如果需
 * 要的话, 你可以取消它.
 */
private fun testCoroutines02() = runBlocking {
    val time = measureTimeMillis {
        val one = async {
            Log.d("testCoroutines02", "doSomethingUsefulOne ${Thread.currentThread()}")
            Log.d("testCoroutines02", "doSomethingUsefulOne ${this.toString()}")
            doSomethingUsefulOne()
        }
        val two = async {
            Log.d("testCoroutines02", "doSomethingUsefulTwo ${Thread.currentThread()}")
            Log.d("testCoroutines02", "doSomethingUsefulTwo ${this.toString()}}")
            doSomethingUsefulTwo()
        }
        Log.d("testCoroutines02", "The answer is ${one.await() + two.await()}")
    }
    Log.d("testCoroutines02", "Completed in $time ms")
}

/**
 * 延迟启动的(Lazily started) async
 *
 * 将可选的 start 参数设置为 CoroutineStart.LAZY, 可以让 async 延迟启动. 只有在通过 await 访问协程的计算结果的时候, 或者调用 start
 * 函数的时候, 才会真正启动协程. 试着运行一下下面的示例程序
 */
private fun testCoroutines03() = runBlocking {
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) {
            Log.d("testCoroutines03", "async doSomethingUsefulOne")
            doSomethingUsefulOne()
        }
        val two = async(start = CoroutineStart.LAZY) {
            Log.d("testCoroutines03", "async doSomethingUsefulTwo")
            doSomethingUsefulTwo()
        }
        // 执行某些计算
        one.start() // 启动第一个协程
        two.start() // 启动第二个协程
        Log.d("testCoroutines03", "The answer is ${one.await() + two.await()}")
    }
    Log.d("testCoroutines03", "Completed in $time ms")
}

/**
 * async 风格的函数
 * 我们可以定义一个 async 风格的函数, 它使用一个明确的 GlobalScope 引用, 通过 async 协程构建器来 异步地 调用
 * doSomethingUsefulOne 和 doSomethingUsefulTwo . 我们将这类函数的名称加上 “Async” 后缀, 明确表示这些函数只负责启动异步
 * 的计算工作, 函数的使用者需要通过函数返回的延期值(deferred value)来得到计算结果
 */

/**
 * 注意, 这些 xxxAsync 函数 不是 挂起 函数. 这些函数可以在任何地方使用.
 * 但是, 使用这些函数总是会隐含着异步执行(这里的意思是 并发)它内部的动作
 */
// somethingUsefulOneAsync 函数的返回值类型是 Deferred<Int>
private fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

// somethingUsefulTwoAsync 函数的返回值类型是 Deferred<Int>
private fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

// 注意, 这个示例中我们没有在 `testCoroutines04` 的右侧使用 `runBlocking
private fun testCoroutines04() {
    val time = measureTimeMillis {
        // 我们可以在协程之外初始化异步操作
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulTwoAsync()

        // 但是等待它的执行结果必然使用挂起或阻塞.
        // 这里我们使用 `runBlocking { ... }`, 在等待结果时阻塞主线程
        runBlocking {
            println("The answer is ${one.await() + two.await()}")
        }
    }
    println("Completed in $time ms")
}

/**
 * 考虑一下, 如果在 val one = somethingUsefulOneAsync() 和 one.await() 表达式之间, 代码存在某种逻辑错误, 程序抛出了一个异常, 程
 * 序的操作中止了, 那么会怎么样. 通常来说, 一个全局的错误处理器可以捕获这个异常, 将这个错误输出到 log, 报告给开发者, 但程序仍然
 * 可以继续运行, 执行其他的操作. 但在这里, 尽管负责启动 somethingUsefulOneAsync 的那部分程序其实已经中止了, 但它仍然会在后台
 * 继续运行. 如果使用结构化并发(structured concurrency)方式话, 就不会发生这种问题, 下面我们来介绍这种方式.
 */

/**
 * 使用 async 的结构化并发
 */

/**
 * 通过这种方式, 如果 concurrentSum 函数内的某个地方发生错误, 抛出一个异常, 那么在这个函数的作用范围内启动的所有协程都会被
 * 取消
 */
private suspend fun concurrentSum(): Int = coroutineScope {
    val one = async {
        doSomethingUsefulOne()
    }
    val two = async {
        doSomethingUsefulTwo()
    }
    one.await() + two.await()
}

private fun testCoroutines05() = runBlocking {
    val time = measureTimeMillis {
        println("The answer is ${concurrentSum()}")
    }
    println("Completed in $time ms")
}

/**
 * 通过协程的父子层级关系, 取消总是会层层传递到所有的子协程, 以及子协程的子协程:
 * 注意, 当一个子协程失败时, 第一个 async , 以及等待子协程的父协程都会被取消:
 */
private suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE) // 模拟一个长时间的计算过程
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}

private fun testCoroutines06() = runBlocking {
    try {
        failedConcurrentSum()
    } catch(e: ArithmeticException) {
        println("Computation failed with ArithmeticException")
    }
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
//    testCoroutines01()
//    testCoroutines02()
//    testCoroutines03()
//    testCoroutines04()
//    testCoroutines05()
    testCoroutines06()
//    testCoroutines07()
//    testCoroutines08()
//    testCoroutines09()
//    testCoroutines10()
}