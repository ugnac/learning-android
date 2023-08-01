package com.learn.workmanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.learn.common.log.LogUtils

/**
 * 后台任务3
 */
class MainWorker3(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        LogUtils.d(TAG, "MainWorker3 doWork: 后台任务执行了")
        return Result.Success() // 本地执行 doWork 任务时 成功 执行任务完毕
    }

    companion object {
        val TAG: String = MainWorker3::class.java.simpleName
    }
}