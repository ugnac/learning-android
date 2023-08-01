package com.learn.aidl.server

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Process
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.learn.common.log.LogUtils

class RemoteService : Service() {
    val TAG = "RemoteService"

    private val REPORT_MSG = 1

    /**
     * Our Handler used to execute operations on the main thread.
     * This is used to schedule increments of our value.
     */
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                REPORT_MSG -> {
                    // Up it goes.
                    val value = ++mValue

                    // Broadcast to all clients the new value.
                    val N = mCallbacks.beginBroadcast()
                    var i = 0
                    while (i < N) {
                        try {
                            mCallbacks.getBroadcastItem(i).valueChanged(value)
                        } catch (e: RemoteException) {
                            // The RemoteCallbackList will take care of removing the dead object for us.
                        }
                        i++
                    }
                    mCallbacks.finishBroadcast()

                    // Repeat every 1 second.
                    sendMessageDelayed(obtainMessage(REPORT_MSG), (1 * 1000).toLong())
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    val mCallbacks: RemoteCallbackList<IRemoteServiceCallback> = RemoteCallbackList<IRemoteServiceCallback>()

    var mValue = 0

    private val mNM: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private val mBinder: IRemoteService.Stub = object : IRemoteService.Stub() {
        override fun registerCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.register(cb)
        }

        override fun unregisterCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.unregister(cb)
        }
    }

    /**
     * A secondary interface to the service.
     */
    private val mSecondaryBinder: ISecondary.Stub = object : ISecondary.Stub() {
        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun basicTypes(
            anInt: Int, aLong: Long, aBoolean: Boolean,
            aFloat: Float, aDouble: Double, aString: String?
        ) {
        }
    }

    override fun onCreate() {
        LogUtils.d(TAG, "onCreate")

        // Display a notification about us starting.
        showNotification()

        // While this service is running, it will continually increment a number.
        // Send the first message that is used to perform the increment.
        mHandler.sendEmptyMessage(REPORT_MSG)
    }

    private fun showNotification() {
        val text = getText(R.string.remote_service_started)

        // The PendingIntent to launch our activity if the user selects this notification
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )

        // Set the info for the views that show in the notification panel.
        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.stat_sample) // the status icon
            .setTicker(text)  // the status text
            .setWhen(System.currentTimeMillis())  // the time stamp
            .setContentTitle(getText(R.string.remote_service_label)) // the label of the entry
            .setContentText(text) // the contents of the entry
            .setContentIntent(pendingIntent) // The intent to send when the entry is clicked
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mNM.notify(R.string.remote_service_started, notification)
        } else {
            LogUtils.d(TAG, "No permission POST_NOTIFICATIONS")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d("RemoteService", "Received start id $startId: $intent")
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        LogUtils.d(TAG, "onBind")
        // Select the interface to return.  If your service only implements a single interface,
        // you can just return it here without checking the Intent.
        if (IRemoteService::class.java.name == intent!!.action) {
            return mBinder
        }

        if (ISecondary::class.java.name == intent.action) {
            return mSecondaryBinder
        }
        return null
    }

    override fun onDestroy() {
        LogUtils.d(TAG, "onDestroy")

        // Cancel the persistent notification.
        mNM.cancel(R.string.remote_service_started)

        // Tell the user we stopped.
        Toast.makeText(this, R.string.remote_service_stopped, Toast.LENGTH_SHORT).show()

    }
}