package com.learn.kotlin

import kotlinx.coroutines.*

/**
 *
 */
class CoroutinesApi {
    fun testCoroutines01() = runBlocking {
        launch { // 启动一个新的协程
            delay(1000L)
            Log.d("testCoroutines01", "World")
        }
        Log.d("testCoroutines01", "Hello")
    }

    fun testCoroutines02() = runBlocking {
        launch { // 启动一个新的协程
            doWorld()
        }
        Log.d("testCoroutines02", "Hello")
    }

    private suspend fun doWorld() {
        delay(1000L)
        Log.d("testCoroutines02", "World")
    }

    fun testCoroutines03() = runBlocking { // runBlocking 阻塞当前线程并等待
        doWorld2()
    }

    private suspend fun doWorld2() = coroutineScope { // coroutineScope 只是挂起，释放底层线程用于其他用途
        launch {
            delay(1000L)
            Log.d("testCoroutines03", "World")
        }
        Log.d("testCoroutines03", "Hello")
    }

    fun testCoroutines04() {
        GlobalScope.launch { // 在后台启动新的协程, 然后继续执行当前程序
            delay(1000L) // 非阻塞, 等待 1 秒 (默认的时间单位是毫秒)
            Log.d("testCoroutines04", "World") // 等待完成后打印信息
        }
        Log.d("testCoroutines04", "Hello") // 当协程在后台等待时, 主线程继续执行
        Thread.sleep(2000L) // 阻塞主线程 2 秒, 保证 JVM 继续存在
    }

    fun testCoroutines05() {
        GlobalScope.launch { // 在后台启动新的协程, 然后继续执行当前程序
            delay(1000L)
            Log.d("testCoroutines05", "World")
        }
        Log.d("testCoroutines05", "Hello") // 主线程在这里立即继续执行
        runBlocking { // 但这个表达式会阻塞主线程
            delay(2000L) // ... 我们在这里等待 2 秒, 保证 JVM 继续存在
        }
    }

    fun testCoroutines06() = runBlocking<Unit> { // 启动主协程
        GlobalScope.launch { // 在后台启动新的协程, 然后继续执行当前程序
            delay(1000L)
            Log.d("testCoroutines06", "World!")
        }
        Log.d("testCoroutines06", "Hello,") // 主协程在这里立即继续执行
        delay(2000L) // 等待 2 秒, 保证 JVM 继续存在
    }

    /**
     * 等待一个任务完成
     * 主协程的代码不必尝试等待一个确定的, 比后台任务运行时间更长的时间. 这样就好多了
     */
    fun testCoroutines07() = runBlocking {
        val job = GlobalScope.launch { // 启动新的协程, 并保存它的执行任务的引用
            delay(1000L)
            Log.d("testCoroutines07", "World!")
        }
        Log.d("testCoroutines07", "Hello,")
        job.join() // 等待, 直到子协程执行完毕
    }

    /**
     * 结构化的并发
     * 所有的协程构建器, 包括 runBlocking , 都会向它的代码段的作用范围添加一个 CoroutineScope 的实例.
     * 我们在这个作用范围内启动协程, 而不需要明确地 join 它们
     */
    fun testCoroutines08() = runBlocking { // this: CoroutineScope
        launch { // 在 runBlocking 的作用范围内启动新的协程
            delay(1000L)
            Log.d("testCoroutines08", "World!")
        }
        Log.d("testCoroutines08", "Hello,")
    }

    /**
     * 作用范围(Scope)构建器
     *
     */
    fun testCoroutines09() = runBlocking { // this: CoroutineScope
        launch {
            delay(200L)
            Log.d("testCoroutines09", "Task from runBlocking")
        }
        coroutineScope { // 创建新的协程作用范围
            launch {
                delay(500L)
                Log.d("testCoroutines09", "Task from nested launch")
            }
            delay(100L)
            Log.d("testCoroutines09", "Task from coroutine scope") // 在嵌套的 launch 之前, 这一行会打印
        }
        Log.d("testCoroutines09", "Coroutine scope is over") // 直到嵌套的 launch 运行结束后, 这一行才会打印
    }

    /**
     * 协程是非常轻量级的
     */
    fun testCoroutines10() = runBlocking {
        repeat(100_000) { // 启动非常多的协程
            launch {
                delay(1000L)
                print(".")
            }
        }
    }

    /**
     * 全局协程类似于守护线程(Daemon Thread)
     * 在 GlobalScope 作用范围内启动的活跃的协程, 不会保持应用程序的整个进程存活. 它们的行为就象守护线程一样
     */
    fun testCoroutines11() = runBlocking {
        GlobalScope.launch {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // 等待一段时间后, 主程序直接退出
    }
}

fun main() {
    val coroutinesApi = CoroutinesApi()
//    coroutinesApi.testCoroutines01()
//    coroutinesApi.testCoroutines02()
//    coroutinesApi.testCoroutines03()
//    coroutinesApi.testCoroutines04()
//    coroutinesApi.testCoroutines05()
//    coroutinesApi.testCoroutines06()
//    coroutinesApi.testCoroutines07()
//    coroutinesApi.testCoroutines08()
//    coroutinesApi.testCoroutines09()
//    coroutinesApi.testCoroutines10()
    coroutinesApi.testCoroutines11()
}

