package com.learn.kotlin

import kotlinx.coroutines.*

/**
 * 协程的取消与超时
 */

private fun testCoroutine01() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 等待一段时间
    println("main: I'm tired of waiting!")
    job.cancel() // 取消 job
    job.join() // 等待 job 结束
//    job.cancelAndJoin() // 取消 并 等待 job 结束
    println("main: Now I can quit.")
}

/**
 * 协程的取消是 协作式的
 * 协程的代码必须与外接配合, 才能够被取消. kotlinx.coroutines 库中的所有挂起函数都是 可取消的.
 * 这些函数会检查协程是否被取消, 并在被取消时抛出 CancellationException 异常.
 * 但是, 如果一个协程正在进行计算, 并且没有检查取消状态, 那么它是不可被取消的
 */
private fun testCoroutine02() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) { // 一个浪费 CPU 的计算任务循环
            // 每秒打印信息 2 次
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // 等待一段时间
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 取消 job, 并等待它结束
    println("main: Now I can quit.")
}

/**
 * 使计算代码能够被取消
 * 有两种方法可以让我们的计算代码变得能够被取消.
 * 第一种办法是定期调用一个挂起函数, 检查协程是否被取消. 有一个 yield 函数可以用来实现这个目的.
 * 另一种方法是显式地检查协程的取消状态. 我们来试试后一种方法
 */
private fun testCoroutine03() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) { // 可被取消的计算循环
            // 每秒打印信息 2 次
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // 等待一段时间
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 取消 job, 并等待它结束
    println("main: Now I can quit.")
}

/**
 * 使用 finally 语句来关闭资源
 * 可被取消的挂起函数, 在被取消时会抛出 CancellationException 异常, 这个异常可以通过异常方式来处理.
 * 比如, 可以使用 try {...} finally {...} 表达式, 或者 Kotlin 的 use 函数, 以便协程被取消时来执行结束处理
 */
private fun testCoroutine04() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        } finally {
//            println("I'm running finally")
            testSuspendFunInFinally() // todo 未按照预期
        }
    }
    delay(1300L) // 等待一段时间
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 取消 job, 并等待它结束
    println("main: Now I can quit.")
}

private suspend fun testSuspendFunInFinally() = coroutineScope { // coroutineScope 只是挂起，释放底层线程用于其他用途
    launch {
        delay(1000L)
        Log.d("testSuspendFunInFinally", "World")
    }
    Log.d("testSuspendFunInFinally", "Hello")
}

/**
 * 运行无法取消的代码段
 * 如果试图在上面示例程序的 finally 代码段中使用挂起函数, 会导致 CancellationException 异常, 因为执行这段代码的协程已被取消了.
 * 通常, 这不是问题, 因为所有正常的资源关闭操作 (关闭文件, 取消任务, 或者关闭任何类型的通信通道) 通常都是非阻塞的, 而且不需要用
 * 到任何挂起函数. 但是, 在极少数情况下, 如果你需要在已被取消的协程中执行挂起操作, 你可以使用 withContext 函数和 NonCancellable
 * 上下文, 把相应的代码包装在 withContext(NonCancellable) {...}
 */
private fun testCoroutine05() = runBlocking {
    Log.d("testCoroutine05", "Enter runBlocking: thread: ${Thread.currentThread()}")
    val job = launch {
        try {
            Log.d("testCoroutine05", "launch: thread: ${Thread.currentThread()}")
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                Log.d("testCoroutine05", "repeat: thread: ${Thread.currentThread()}")
                delay(500L)
            }
        } finally {
            withContext(NonCancellable) {
                println("I'm running finally")
                delay(1000L)
                println("And I've just delayed for 1 sec because I'm non-cancellable")
            }
        }
    }
    delay(1300L) // 等待一段时间
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // 取消 job, 并等待它结束
    println("main: Now I can quit.")
}

/**
 * 超时
 * 在实际应用中, 取消一个协程最明显的理由就是, 它的运行时间超过了某个时间限制. 当然, 你可以手动追踪协程对应的 Job, 然后启动另一
 * 个协程, 在等待一段时间之后取消你追踪的那个协程, 但 Kotlin 已经提供了一个 withTimeout 函数来完成这个任务.
 */
private fun testCoroutine06() = runBlocking {
    withTimeout(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
}

private fun testCoroutine07() = runBlocking {
    val result = withTimeoutOrNull(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
        "Done" // 协程会在输出这个消息之前被取消
    }
    println("Result is $result")
}

fun main() {
//    testCoroutine01()
//    testCoroutine02()
//    testCoroutine03()
//    testCoroutine04()
//    testCoroutine05()
//    testCoroutine06()
    testCoroutine07()
}
