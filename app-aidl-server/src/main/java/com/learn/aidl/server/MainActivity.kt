package com.learn.aidl.server

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.learn.aidl.server.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val startHello = View.OnClickListener {
        startService(Intent(this, HelloService::class.java))
    }

    private val stopHello = View.OnClickListener {
        // 如果还有其他绑定的客户端client, 此时该服务并不会真正的停止
        stopService(Intent(this, HelloService::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val startRemote = View.OnClickListener {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission()
        } else {
            startService(Intent(this, RemoteService::class.java))
        }
    }

    private val stopRemote = View.OnClickListener {
        stopService(Intent(this, RemoteService::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.startHello.setOnClickListener(startHello)
        binding.stopHello.setOnClickListener(stopHello)
        binding.startRemote.setOnClickListener(startRemote)
        binding.stopRemote.setOnClickListener(stopRemote)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(Intent(this, RemoteService::class.java))
            }
        }
    }

    companion object {
        const val REQUEST_CODE_POST_NOTIFICATIONS = 1
    }
}