package com.learn.kotlin

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

/**
 * 协程上下文与派发器(Dispatcher)
 *
 * 派发器与线程
 * 协程上下文包含了一个 协程派发器 (参见 CoroutineDispatcher), 它负责确定对应的协程使用哪个或哪些线程来执行. 协程派发器可以将
 * 协程的执行限定在某个特定的线程上, 也可以将协程的执行派发给一个线程池, 或者不加限定, 允许协程运行在任意的线程上.
 * 所有的协程构建器, 比如 launch 和 async, 都接受一个可选的 CoroutineContext 参数, 这个参数可以用来为新创建的协程显式地指定派发
 * 器, 以及其他上下文元素.
 */

private fun testCoroutines01() = runBlocking {
    launch { // 使用父协程的上下文, 也就是 main 函数中的 runBlocking 协程
        println("main runBlocking : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Unconfined) { // 非受限 -- 将会在主线程中执行
        println("Unconfined : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Default) { // 会被派发到 DefaultDispatcher
        println("Default : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(newSingleThreadContext("MyOwnThread")) { // 将会在独自的新线程内执行
        println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
    }
}

/**
 * 非受限派发器(Unconfined dispatcher)与受限派发器(Confined dispatcher)
 *
 * Dispatchers.Unconfined 协程派发器会在调用者线程内启动协程, 但只会持续运行到第一次挂起点为止. 在挂起之后, 它会在哪个线程内
 * 恢复执行, 这完全由被调用的挂起函数来决定. 非受限派发器(Unconfined dispatcher) 适用的场景是, 协程不占用 CPU 时间, 也不更新那
 * 些限定于某个特定线程的共享数据(比如 UI).
 *
 * 另一方面, 默认情况下, 会继承外层 CoroutineScope 的派发器. 具体来说, 对于 runBlocking 协程, 默认的派发器会限定为调用它的那个线
 * 程, 因此继承这个派发器的效果就是, 将协程的执行限定在这个线程上, 并且执行顺序为可预测的先进先出(FIFO)调度顺序.
 */
private fun testCoroutines02() = runBlocking {
    launch(Dispatchers.Unconfined) { // 非受限 -- 将会在主线程中执行
        println("Unconfined : I'm working in thread ${Thread.currentThread().name}")
        delay(500)
        println("Unconfined : After delay in thread ${Thread.currentThread().name}")
    }
    launch { // 使用父协程的上下文, 也就是 main 函数中的 runBlocking 协程
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }
}

/**
 * 协程与线程的调试
 * 协程可以在一个线程内挂起, 然后在另一个线程中恢复运行. 即使协程的派发器只使用一个线程, 也很难弄清楚协程在哪里, 在什么时间,
 * 具体做了什么操作. 调试多线程应用程序的常见办法是在日志文件的每一条日志信息中打印线程名称. 在各种日志输出框架中都广泛的支
 * 持这个功能. 在使用协程时, 仅有线程名称还不足以确定协程的上下文, 因此 kotlinx.coroutines 包含了一些调试工具来方便我们的调试
 * 工作
 */
private fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

/**
 * 协程与线程的调试
 * 请使用 JVM 选项 -Dkotlinx.coroutines.debug 来运行下面的示例程序:
 */
private fun testCoroutines03() = runBlocking {
    val a = async {
        log("I'm computing a piece of the answer")
        6
    }
    val b = async {
        log("I'm computing another piece of the answer")
        7
    }
    log("The answer is ${a.await() * b.await()}")
}

/**
 * 在线程间跳转
 * 请使用 JVM 参数 -Dkotlinx.coroutines.debug 运行下面的示例程序
 *
 * 下面的示例程序演示了几种技巧. 一是使用明确指定的上下文来调用 runBlocking, 另一个技巧是使用 withContext 函数, 在同一个协程内
 * 切换协程的上下文, 运行结果如下, 你可以看到切换上下文的效果:
 */
private fun testCoroutines04() {
    // 注意, 这个示例程序还使用了 Kotlin 标准库的 use 函数, 以便在 newSingleThreadContext 创建的线程不再需要的时候释放它
    newSingleThreadContext("Ctx1").use { ctx1 ->
        newSingleThreadContext("Ctx2").use { ctx2 ->
            runBlocking(ctx1) {
                log("Started in ctx1")
                withContext(ctx2) {
                    log("Working in ctx2")
                }
                log("Back to ctx1")
            }
        }
    }
}

/**
 * 在上下文中的任务
 * 协程的 Job 是协程上下文的一部分. 协程可以通过自己的上下文来访问到 Job, 方法是使用 coroutineContext[Job] 表达式:
 */
private fun testCoroutines05() = runBlocking {
    println("My job is ${coroutineContext[Job]}")
}

/**
 * 协程的子协程
 *
 * 当一个协程在另一个协程的 CoroutineScope 内启动时, 它会通过 CoroutineScope.coroutineContext 继承这个协程的上下文, 并且新协
 * 程的 Job 会成为父协程的任务的一个 子任务. 当父协程被取消时, 它所有的子协程也会被取消, 并且会逐级递归, 取消子协程的子协程.
 *
 * 但是, 如果使用 GlobalScope 来启动一个协程, 那么这个协程不会被绑定到启动它的那段代码的作用范围, 并会独自运行.
 */
private fun testCoroutines06() = runBlocking {
    // 启动一个协程, 处理某种请求
    val request = launch {
        // 它启动 2 个其他的任务, 其中一个使用 GlobalScope
        GlobalScope.launch {
            Log.d(msg = "job1: I run in GlobalScope and execute independently!")
            delay(1000)
            Log.d(msg = "job1: I am not affected by cancellation of the request")
        }

        // 另一个继承父协程的上下文
        launch {
            delay(100)
            Log.d(msg = "job2: I am a child of the request coroutine")
            delay(1000)
            Log.d(msg = "job2: I will not execute this line if my parent request is cancelled")
        }
    }
    delay(500)
    request.cancel() // 取消对请求的处理
    delay(1000) // 延迟 1 秒, 看看结果如何
    Log.d(msg = "main: Who has survived request cancellation?")
}


/**
 * 父协程的职责
 * 父协程总是会等待它的所有子协程运行完毕. 父协程不必明确地追踪它启动的子协程, 也不必使用 Job.join 来等待子协程运行完毕:
 */
private fun testCoroutines07() = runBlocking {
    // 启动一个协程, 处理某种请求
    val request = launch {
        repeat(3) { i -> // 启动几个子协程
            launch {
                delay((i + 1) * 200L) // 各个子协程分别等待 200ms, 400ms, 600ms
                println("Coroutine $i is done")
            }
        }
        println("request: I'm done and I don't explicitly join my children that are still active")
    }
    request.join() // 等待 request 协程执行完毕, 包括它的所有子协程
    println("Now processing of the request is complete")
}

/**
 * 为协程命名以便于调试
 * 如果协程频繁输出日志, 而且你只需要追踪来自同一个协程的日志, 那么使用系统自动赋予的协程 id 就足够了. 然而, 如果协程与某个特定
 * 的输入处理绑定在一起, 或者负责执行某个后台任务, 那么最好明确地为协程命名, 以便于调试. 对协程来说, 上下文元素 CoroutineName
 * 起到与线程名类似的作用. 当 调试模式 开启时, 协程名称会出现在正在运行这个协程的线程的名称内.
 */
private fun testCoroutines08() = runBlocking(CoroutineName("main")) {
    log("Started main coroutine")
    // 启动 2 个背景任务
    val v1 = async(CoroutineName("v1-coroutine")) {
        delay(500)
        log("Computing v1")
        252
    }
    val v2 = async(CoroutineName("v2-coroutine")) {
        delay(1000)
        log("Computing v2")
        6
    }
    log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
}

/**
 * 组合上下文中的元素
 * 有些时候我们会需要对协程的上下文定义多个元素. 这时我们可以使用 + 操作符. 比如, 我们可以同时使用明确指定的派发器, 以及明确
 * 指定的名称, 来启动一个协程
 */
private fun testCoroutines09() = runBlocking {
    launch(Dispatchers.Default + CoroutineName("test")) {
        println("I'm working in thread ${Thread.currentThread().name}")
    }
}

/**
 * 使用显式任务来取消协程
 * 下面我们把关于上下文, 子协程, 任务的相关知识综合起来. 假设我们的应用程序中有一个对象, 它存在一定的生命周期, 但这个对象不是
 * 一个协程. 比如, 我们在编写一个 Android 应用程序, 在一个 Android activity 的上下文内启动了一些协程, 执行一些异步操作, 来取得并更
 * 新数据, 显示动画, 等等等等. 当 activity 销毁时, 所有这些协程都必须取消, 以防内存泄漏.
 *
 * 我们创建 Job 的实例, 并将它与 activity 的生命周期相关联, 以此来管理协程的生命周期. 当 activity 创建时, 使用工厂函数 Job() 来创建任
 * 务的实例, 当 activity 销毁时取消这个任务, 如下例所示
 */
private class Activity : CoroutineScope {
    lateinit var job: Job
    fun create() {
        job = Job()
    }

    fun destroy() {
        job.cancel()
    }

    // 待续 ...
    // Activity 类的内容继续
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    // 待续 ...
    // Activity 类的内容继续
    fun doSomething() {
        // 启动 10 个协程, 每个工作一段不同长度的时间
        repeat(10) { i ->
            launch {
                delay((i + 1) * 200L) // 分别延迟 200ms, 400ms, ... 等等
                println("Coroutine $i is done")
            }
        }
    }
} // Activity 类结束

private fun testCoroutines10() = runBlocking<Unit> {
    val activity = Activity()
    activity.create() // 创建一个 activity
    activity.doSomething() // 运行测试函数
    println("Launched coroutines")
    delay(500L) // 等待半秒
    println("Destroying activity!")
    activity.destroy() // 取消所有协程
    delay(1000) // 确认协程不再继续工作
}


/**
 * 线程的局部数据
 * 有些时候, 如果能够传递一些线程局部的数据(thread-local data)将是一种很方便的功能, 但是对于协程来说, 它并没有关联到某个具体的
 * 线程, 因此, 不写大量的样板代码, 通常很难自己写代码来实现这个功能.
 *
 * 对于 ThreadLocal, 有一个扩展函数 asContextElement 可以帮助我们. 它会创建一个额外的上下文元素, 用来保持某个给定的
 * ThreadLocal 的值, 并且每次当协程切换上下文时就恢复它的值
 */

private val threadLocal = ThreadLocal<String?>() // 声明线程局部变量

/**
 * ThreadLocal 在协程中得到了一级支持, 可以在 kotlinx.coroutines 提供的所有基本操作一起使用. 它只有一个关键的限制: 当线程局部
 * 变量的值发生变化时, 新值不会传递到调用协程的线程中去 (因为上下文元素不能追踪对 ThreadLocal 对象的所有访问) 而且更新后的值
 * 会在下次挂起时丢失. 请在协程内使用 withContext 来更新线程局部变量的值, 详情请参见 asContextElement.
 *
 * 另一种方法是, 值可以保存在可变的装箱类(mutable box)中, 比如 class Counter(var i: Int) , 再把这个装箱类保存在线程局部变量中. 然
 * 而, 这种情况下, 对这个装箱类中的变量可能发生并发修改, 你必须完全负责对此进行同步控制.
 */
private fun testCoroutines11() = runBlocking {
    threadLocal.set("main")
    println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
        println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        yield()
        println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
    job.join()
    println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
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
//    testCoroutines10()
    testCoroutines11()
}