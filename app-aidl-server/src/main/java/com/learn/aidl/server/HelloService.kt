package com.learn.aidl.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log
import android.widget.Toast
import com.learn.common.log.LogUtils

class HelloService : Service() {
    private val students: ArrayList<Student> by lazy {
        ArrayList<Student>().apply {
            add(Student("张三丰", "男", 90, 120))
            add(Student("王重阳", "男", 80, 110))
            add(Student("小龙女", "女", 20, 100))
            add(Student("黄蓉", "女", 22, 101))
        }
    }

    private val binder = object : IHelloService.Stub() {
        override fun getPid(): Int {
            LogUtils.d(TAG, "getPid")
            return Process.myPid()
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            LogUtils.d(TAG, "basicTypes")
        }

        override fun addStudent(student: Student?) {
            LogUtils.d(TAG, "addStudent")
            student ?: return
            students.add(student)
        }

        override fun getStudentList(): MutableList<Student> {
            LogUtils.d(TAG, "getStudentList")
            return students
        }

    }

    override fun onCreate() {
        LogUtils.d(TAG, "onCreate")
        Toast.makeText(this, "Hello Service 已启动", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? {
        LogUtils.d(TAG, "onBind")
        return binder
    }

    override fun onDestroy() {
        LogUtils.d(TAG, "onDestroy")
        Toast.makeText(this, "Hello Service 已停止", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "HelloService"
    }
}