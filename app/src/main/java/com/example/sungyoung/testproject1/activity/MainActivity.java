package com.example.sungyoung.testproject1.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sungyoung.testproject1.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable alpha = ((ImageView)findViewById(R.id.logoImageView)).getDrawable();

        Button openbankBtn = (Button) findViewById(R.id.diaryBtn);
        openbankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "diary시작하기", Toast.LENGTH_SHORT);
            }
        });

        Button accountBtn = (Button) findViewById(R.id.accountBtn);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountBookActivity.class);
                startActivity(intent);
            }
        });
    }
}




