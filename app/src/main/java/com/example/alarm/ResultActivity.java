package com.example.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.util.PreferenceUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity  extends AppCompatActivity {

    AsyncHttpRequest async=new AsyncHttpRequest(this);

    PreferenceUtil pref;
    /**
     * 画面を表示する.
     *  note:デフォルトで実装されている
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        pref = new PreferenceUtil(this);
        try {
            async.execute(new URL("https://weather.tsukumijima.net/api/forecast/city/440010"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){//クリックしたら最初の画面に戻る
                Intent intent = new Intent(getApplication(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

