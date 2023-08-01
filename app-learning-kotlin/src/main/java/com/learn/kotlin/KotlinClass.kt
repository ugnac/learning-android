package com.learn.kotlin

class KotlinClass {
    companion object {
        private const val TAG = "KotlinClass"

        @JvmField val INTEGER_ONE = 1

        fun doWork() {
           Log.d(TAG, "doWork")
        }

        @JvmStatic
        fun doWork2() {
            Log.d(TAG, "doWork")
        }
    }
}