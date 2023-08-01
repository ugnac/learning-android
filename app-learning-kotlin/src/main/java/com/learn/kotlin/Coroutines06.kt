package com.learn.kotlin

import kotlinx.coroutines.*
import java.io.IOException
import kotlin.system.measureTimeMillis

/**
 * 异常处理
 *
 * 异常的传播(propagation)
 * 协程构建器对于异常的处理有两种风格: 自动传播异常(launch 和 actor 构建器), 或者将异常交给使用者处理(async 和 produce 构建器).
 * 前一种方式部队异常进行处理, 类似于 Java 的 Thread.uncaughtExceptionHandler , 后一种则要求使用者处理最终的异常, 比如使用
 * await 或 receive 来处理异常. (关于 produce 和 receive 请参见 通道(Channel)).
 */

private fun testCoroutines01() = runBlocking {
    val job = GlobalScope.launch {
        println("Throwing exception from launch")
        throw IndexOutOfBoundsException() // 这个异常会被 Thread.defaultUncaughtExceptionHandler 打印到控制台
    }
    job.join()
    println("Joined failed job")
    val deferred = GlobalScope.async {
        println("Throwing exception from async")
        throw ArithmeticException() // 这个异常不会被打印, 由使用者调用 await 来得到并处理这个异常
    }
    try {
        deferred.await()
        println("Unreached")
    } catch (e: ArithmeticException) {
        println("Caught ArithmeticException")
    }
}

/**
 * CoroutineExceptionHandler
 * 但是如果我们不想把所有的异常都输出到控制台, 那么应该怎么办呢? 协程的上下文元素 CoroutineExceptionHandler 会被作为协程的通
 * 用的 catch 块, 我们可以在这里实现自定义的日志输出, 或其他异常处理逻辑. 它的使用方法与 Thread.uncaughtExceptionHandler 类似.
 *
 * 在 JVM 平台, 可以通过 ServiceLoader 注册一个 CoroutineExceptionHandler, 为所有的协程重定义全局的异常处理器. 全局的异常处理器
 * 类似于 Thread.defaultUncaughtExceptionHandler, 如果没有注册更具体的异常处理器, 就会使用这个
 * Thread.defaultUncaughtExceptionHandler 异常处理器. 在 Android 平台, 默认安装的协程全局异常处理器是
 * uncaughtExceptionPreHandler .
 *
 * 有些异常是我们预计会被使用者处理的, 只有发生了这类异常以外的其他异常时, 才会调用 CoroutineExceptionHandler, 因此, 对 async
 * 或其他类似的协程构建器注册异常处理器, 不会产生任何效果
 */
private fun testCoroutines02() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    val job = GlobalScope.launch(handler) {
        throw AssertionError()
    }
    val deferred = GlobalScope.async(handler) {
        throw ArithmeticException() // 这个异常不会被打印, 由使用者调用 deferred.await() 来得到并处理这个异常
    }
    joinAll(job, deferred)
}

/**
 * 取消与异常
 *
 * 协程的取消与异常有着非常紧密的关系. 协程内部使用 CancellationException 来实现取消, 这些异常会被所有的异常处理器忽略, 因此
 * 它们只能用来在 catch 块中输出额外的调试信息. 如果使用 Job.cancel 来取消一个协程, 而且不指明任何原因, 那么协程会终止运行, 但
 * 不会取消它的父协程. 父协程可以使用不指明原因的取消机制, 来取消自己的子协程, 而不取消自己.
 */
private fun testCoroutines03() = runBlocking {
    val job = launch {
        val child = launch {
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Child is cancelled")
            }
        }
        yield()
        println("Cancelling child")
        child.cancel()
        child.join()
        yield()
        println("Parent is not cancelled")
    }
    job.join()
}

/**
 * 如果一个协程遇到了 CancellationException 以外的异常, 那么它会使用这个异常来取消自己的父协程. 这种行为不能覆盖, 而且 Kotlin
 * 使用这个机制来实现 结构化并发 中的稳定的协程层级关系, 而不是依赖于 CoroutineExceptionHandler 的实现. 当所有的子协程全部结
 * 束后, 原始的异常会被父协程处理
 *
 * 这也是为什么, 在这些示例程序中, 我们总是在 GlobalScope 内创建的协程上安装 CoroutineExceptionHandler. 如果在 main
 * runBlocking 的作用范围内启动的协程上安装异常处理器, 是毫无意义的, 因为子协程由于异常而终止后之后, 主协程一定会被取消,
 * 而忽略它上面安装的异常处理器.
 */
private fun testCoroutines04() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    val job = GlobalScope.launch(handler) {
        launch { // 第 1 个子协程
            try {
                delay(Long.MAX_VALUE)
            } finally {
                withContext(NonCancellable) {
                    println("Children are cancelled, but exception is not handled until all children terminate")
                    delay(100)
                    println("The first child finished its non cancellable block")
                }
            }
        }
        launch { // 第 2 个子协程
            delay(10)
            println("Second child throws an exception")
            throw ArithmeticException()
        }
    }
    job.join()
}

/**
 * 异常的聚合(aggregation)
 *
 * 如果一个协程的多个子协程都抛出了异常, 那么会怎么样? 通常的规则是 “最先发生的异常优先”, 因此第 1 个发生的异常会被传递给异
 * 常处理器. 但是这种处理发生可能会导致丢失其他异常, 比如, 如果另一个协程在它的 finally 块中抛出了另一个异常. 为了解决这个问题,
 * 我们将其他异常压制(suppress)到最先发生的异常内.
 */
private fun testCoroutines05() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception with suppressed ${exception.suppressed.contentToString()}")
    }
    val job = GlobalScope.launch(handler) {
        launch {
            try {
                delay(Long.MAX_VALUE)
            } finally {
                throw ArithmeticException()
            }
        }
        launch {
            delay(100)
            throw IOException()
        }
        delay(Long.MAX_VALUE)
    }
    job.join()
}

/**
 * 协程取消异常是透明的, 默认不会被聚合到其他异常中:
 */
private fun testCoroutines06() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught original $exception")
    }
    val job = GlobalScope.launch(handler) {
        val inner = launch {
            launch {
                launch {
                    throw IOException()
                }
            }
        }
        try {
            inner.join()
        } catch (e: CancellationException) {
            println("Rethrowing CancellationException with original cause")
            throw e
        }
    }
    job.join()
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
    testCoroutines04()
    testCoroutines05()
//    testCoroutines06()
//    testCoroutines07()
//    testCoroutines08()
//    testCoroutines09()
//    testCoroutines10()
}