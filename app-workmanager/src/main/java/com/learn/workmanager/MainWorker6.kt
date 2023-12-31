package com.learn.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * 后台任务6
 */
class MainWorker6(private val context: Context, private val workerParams: WorkerParameters) : Worker(context, workerParams) {
    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        Log.d(TAG, "MainWorker6 doWork: 后台任务执行了")
        return Result.Success() // 本地执行 doWork 任务时 成功 执行任务完毕
    }

    companion object {
        val TAG = MainWorker6::class.java.simpleName
    }
}