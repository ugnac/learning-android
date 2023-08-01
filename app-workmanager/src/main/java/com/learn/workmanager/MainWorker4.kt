package com.learn.workmanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.learn.common.log.LogUtils

/**
 * 后台任务4
 */
class MainWorker4
    (private val context: Context, private val workerParams: WorkerParameters) : Worker(context, workerParams) {
    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        LogUtils.d(TAG, "MainWorker4 doWork: 后台任务执行了")
        return Result.Success() // 本地执行 doWork 任务时 成功 执行任务完毕
    }

    companion object {
        val TAG: String = MainWorker4::class.java.simpleName
    }
}