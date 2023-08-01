package com.learn.kotlin

object Log {
    private const val DEBUG = true

    fun d(tag: String = "KotlinX", msg: Any?) {
        if (DEBUG) {
            println("$tag: [Thread: ${Thread.currentThread().name}]: [Msg: $msg]")
        } else {
            println("$tag: $msg")
        }
    }
}