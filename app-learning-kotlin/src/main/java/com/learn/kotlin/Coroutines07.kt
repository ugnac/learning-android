package com.learn.kotlin

import kotlinx.coroutines.*
import java.io.IOException
import kotlin.system.measureTimeMillis

/**
 * 监控
 *
 * 正如我们前面学到的, 取消是一种双向关系, 它会在整个协程层级关系内传播. 但是如果我们需要单向的取消, 那么应该怎么办呢?
 *
 * 这种需求的一个很好的例子就是一个 UI 组件, 在它的作用范围内定义了一个任务. 如果 UI 的任何一个子任务失败, 并不一定有必要取消
 * (最终效果就是杀死) 整个 UI 组件, 但是如果 UI 组件本身被销毁(而且它的任务也被取消了), 那么就有必要终止所有的子任务, 因为子任务
 * 的结果已经不再需要了.
 *
 * 另一个例子是, 一个服务器进程启动了几个子任务, 需要 监控 这些子任务的执行, 追踪它们是否失败, 并且重启那些失败的子任务.
 *
 * 监控任务
 * 为了这类目的, 我们可以使用 SupervisorJob. 它与通常的 Job 类似, 唯一的区别在于取消只向下方传播. 我们用一个示例程序来演示一下:
 */

private fun testCoroutines01() = runBlocking {
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        // 启动第 1 个子协程 -- 在这个示例程序中, 我们会忽略它的异常 (实际应用中不要这样做!)
        val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
            println("First child is failing")
            throw AssertionError("First child is cancelled")
        }
        // 启动第 2 个子协程
        val secondChild = launch {
            firstChild.join()
            // 第 1 个子协程的取消不会传播到第 2 个子协程
            println("First child is cancelled: ${firstChild.isCancelled}, but second one is still active")
            try {
                delay(Long.MAX_VALUE)
            } finally {
                // 但监控任务的取消会传播到第 2 个子协程
                println("Second child is cancelled because supervisor is cancelled")
            }
        }
        // 等待第 1 个子协程失败, 并结束运行
        firstChild.join()
        println("Cancelling supervisor")
        supervisor.cancel()
        secondChild.join()
    }
}


/**
 * 监控作用范围
 * 对于 带作用范围 的并发, 可以使用 supervisorScope 代替 coroutineScope 来实现同一目的. 它也只向一个方向传播取消, 并且只在它自
 * 身失败的情况下取消所有的子协程. 它和 coroutineScope 一样, 在运行结束之前也会等待所有的子协程结束.
 */
private fun testCoroutines02() = runBlocking {
    try {
        supervisorScope {
            val child = launch {
                try {
                    println("Child is sleeping")
                    delay(Long.MAX_VALUE)
                } finally {
                    println("Child is cancelled")
                }
            }
            // 使用 yield, 给子协程一个机会运行, 并打印信息
            yield()
            println("Throwing exception from scope")
            throw AssertionError()
        }
    } catch (e: AssertionError) {
        println("Caught assertion error")
    }
}

/**
 * 被监控的协程中的异常
 * 常规任务与监控任务的另一个重要区别就是对异常的处理方式. 每个子协程都应该通过异常处理机制自行处理它的异常. 区别在于, 子协
 * 程的失败不会传播到父协程中
 */
private fun testCoroutines03() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    supervisorScope {
        val child = launch(handler) {
            println("Child throws an exception")
            throw AssertionError()
        }
        println("Scope is completing")
    }
    println("Scope is completed")
}


private fun testCoroutines04() = runBlocking {

}


private fun testCoroutines05() = runBlocking {
}

private fun testCoroutines06() = runBlocking {
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
//    testCoroutines03()
//    testCoroutines04()
//    testCoroutines05()
//    testCoroutines06()
//    testCoroutines07()
//    testCoroutines08()
//    testCoroutines09()
//    testCoroutines10()
}