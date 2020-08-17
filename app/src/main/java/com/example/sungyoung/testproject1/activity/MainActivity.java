package com.example.sungyoung.testproject1.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungyoung.testproject1.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button openbankBtn = null;
    Button accountBtn = null;
    TextView loadTextView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable alpha = ((ImageView)findViewById(R.id.logoImageView)).getDrawable();
        loadTextView = (TextView)findViewById(R.id.loadTextView);
        startLoading();
    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                String loadStr = loadTextView.getText().toString();
                if(loadStr.length() > 9){
                    return;
                }
                //로딩 중 텍스트 점 추가하기
                loadTextView.setText(loadStr + ".");
            }
        },0,1000);
    }
/*
    public void initListener(){
        openbankBtn = (Button) findViewById(R.id.diaryBtn);
        openbankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "diary시작하기", Toast.LENGTH_SHORT);
            }
        });

        accountBtn = (Button) findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountBookActivity.class);
                startActivity(intent);
            }
        });
    }*/
}




