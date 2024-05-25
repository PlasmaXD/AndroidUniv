package com.example.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarm.util.PreferenceUtil;

public class PlayLightActivity extends AppCompatActivity {
    Button stop;
    Boolean hascameralight =false;//そもそも端末にライトが付いているか
    Boolean flashon =false;//ライトが点灯してるかどうかのフラグ
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play_light);

//そもそも端末にライトが付いているか
        hascameralight=getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (hascameralight){
            if(!flashon) {//ライトがもし消えてるならライト点灯

                flashon=true;
                try {
                    lighton();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }


        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {//ライトを消して初期画面に戻る
            if (hascameralight){
                if(flashon){
                    flashon=false;
                    try {
                        lightoff();//ライトを消して
                        PreferenceUtil pref = new PreferenceUtil(PlayLightActivity.this);
                        pref.delete(MainActivity.ALARM_TIME);//アラームのセットを解除
                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        startActivity(intent);//初期画面に戻る
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
           }
            else {

            }
        }
        });
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void lighton() throws CameraAccessException {
        CameraManager cameraManager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camid=cameraManager.getCameraIdList()[0];//取得したカメラのID
        cameraManager.setTorchMode(camid,true);//カメラを開かずにライトを付ける(トーチモード)をon
        Toast.makeText(this, "flash is on  ", Toast.LENGTH_SHORT).show();


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void lightoff() throws CameraAccessException {
        CameraManager cameraManager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camid=cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(camid,false);
        Toast.makeText(this, "flash is off ", Toast.LENGTH_SHORT).show();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();
    }
}
