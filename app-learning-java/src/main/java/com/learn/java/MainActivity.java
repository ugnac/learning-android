package com.learn.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyThread t = new MyThread(this);
        t.start();
    }

    class MyThread extends Thread {
        Context context;

        MyThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}