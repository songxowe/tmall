package com.tmall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).
                inflate(R.layout.activity_splash,null);
        setContentView(view);

        // 渐变动画
        AlphaAnimation animation = new AlphaAnimation(0.3f,1.0f);
        animation.setDuration(1500);
        view.setAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 设置休眠时长,用于完成动画
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 由开屏页跳转至指定页面
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                SplashActivity.this.finish();  // 销毁开屏页
            }
        }).start();
    }
}
