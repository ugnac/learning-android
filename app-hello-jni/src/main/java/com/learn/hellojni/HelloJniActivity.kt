package com.learn.hellojni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.learn.hellojni.databinding.ActivityHelloJniBinding

class HelloJniActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHelloJniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.helloTextview.text = stringFromJNI()
        binding.helloTextview.setOnClickListener {
            startActivity(Intent(this, Plasma::class.java))
        }
    }

    private external fun stringFromJNI(): String?

    companion object {
        /*
         * this is used to load the 'hello-jni' library on application
         * startup. The library has already been unpacked into
         * /data/data/com.example.hellojni/lib/libhello-jni.so
         * at the installation time by the package manager.
         */
        init {
            System.loadLibrary("hello-jni")
            System.loadLibrary("plasma")
        }
    }
}