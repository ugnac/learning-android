package com.learn.aidl.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import com.learn.aidl.client.databinding.ActivityMainBinding
import com.learn.aidl.server.IHelloService
import com.learn.aidl.server.IRemoteService
import com.learn.aidl.server.Student
import com.learn.common.log.LogUtils

class MainActivity : AppCompatActivity() {
    private var isHelloBind = false

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val bindHello = View.OnClickListener {
        LogUtils.d(TAG, "Client bindHello")
        val intent = Intent().apply {
            action = "com.learn.aidl.server.HelloService"
            `package` = "com.learn.aidl.server"
        }
        bindService(intent, helloConnection, BIND_AUTO_CREATE)
    }

    private val unbindHello = View.OnClickListener {
        LogUtils.d(TAG, "Client unbindHello: helloService $helloService")
        if (helloService != null) {
            unbindService(helloConnection)
        }
    }

    private val bindRemote = View.OnClickListener {
        val intent = Intent().apply {
            component =
                ComponentName("com.learn.aidl.server", "com.learn.aidl.server.RemoteService")
        }
        bindService(intent, remoteConnection, BIND_AUTO_CREATE)
    }

    private val unbindRemote = View.OnClickListener {
        unbindService(remoteConnection)
    }

    var remoteService: IRemoteService? = null

    var helloService: IHelloService? = null

    private val helloConnection = object : ServiceConnection {
        /**
         * Called when the connection with the service is established
         * 与服务建立连接时调用
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogUtils.d(TAG, "helloConnection 连接成功")

            // this gets an instance of the IHelloService, which we can use to call on the service
            helloService = IHelloService.Stub.asInterface(service)
            LogUtils.d(TAG, " helloService.pid = ${helloService?.pid}")
            helloService?.addStudent(Student("张敏", "女", 18, 102))
        }

        /**
         * Called when the connection with the service disconnects unexpectedly
         */
        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtils.d(TAG, "helloConnection 断开连接")
            helloService = null
        }
    }

    private val remoteConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            remoteService = IRemoteService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            remoteService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.startHello.setOnClickListener(bindHello)
        binding.stopHello.setOnClickListener(unbindHello)
        binding.startRemote.setOnClickListener(bindRemote)
        binding.stopRemote.setOnClickListener(unbindRemote)
    }

    companion object {
        private const val TAG = "ClientMainActivity"
    }
}