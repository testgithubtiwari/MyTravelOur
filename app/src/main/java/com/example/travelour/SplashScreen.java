package com.example.travelour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private TextView tv1spalsh;
    private String mTextToType = "Welcome to Travel Our App ";
    private Handler mHandler;
    private Runnable mTypeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tv1spalsh=findViewById(R.id.tv1spalsh);

        tv1spalsh.setText("");

        mHandler = new Handler();
        mTypeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mTextToType.length() > 0) {
                    tv1spalsh.setText(tv1spalsh.getText().toString() + mTextToType.charAt(0));
                    mTextToType = mTextToType.substring(1);
                    mHandler.postDelayed(mTypeRunnable, 70); // Change the delay time to adjust typing speed
                }
            }
        };
        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    sleep(3000);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                Intent intent=new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
                SplashScreen.this.finish();
            }
        }.start();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mTypeRunnable, 1000); // Change the delay time to adjust when the typing starts
    }

    }
