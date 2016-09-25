package com.example.zouxiaobang.circleprogress;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.example.zouxiaobang.circleprogress.userdefinedview.CircleProgress;

public class MainActivity extends Activity {
    private CircleProgress cp;
    private int progress;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            cp.setProgress(progress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cp = (CircleProgress) findViewById(R.id.cp_test);

        new Thread(){
            @Override
            public void run() {
                while (progress < 100){
                    progress ++;
                    mHandler.sendEmptyMessage(1);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
