package com.example.alarm;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;


import com.example.alarm.util.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@RequiresApi(api = Build.VERSION_CODES.N)
public class PlaySoundActivity extends AppCompatActivity implements SensorEventListener,Runnable {
    //タイマー関連
   // https://github.com/Ogushi-Tomoki/ShakeAlarmより
    private Handler timerhandler = new Handler();//光の当たっている時間のカウント
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            count ++;
            timerText.setText(dataFormat.format((brightTime * 10 - count)*period));
            timerhandler.postDelayed(this, period);//period後にtimerhandlerを実行　　//postDelayed(Runnable r, long delayMillis)
        }
    };

    private TextView timerText;
    private SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss.S", Locale.US);//残り時間の書式をminite:second:milisecond
    private int count, period;
    private final static String TAG = PlaySoundActivity.class.getSimpleName();

    //明るさゲージ
    private ProgressBar bar;
    //mp3音源
    private MediaPlayer mp;
    private TextView infoView;
    private TextView timeView;
    public TextView tv;
    //タイマーを止めるのに振り続けなければいけない時間（秒）
    private static final int brightTime = 6;

    //センサー関連
    private static final float ALPHA = 0.75f;

    private SensorManager manager;
    private Sensor sensor;

    private final Handler handler = new Handler();//UIスレッド用
    private final Timer timer = new Timer();//

    private float rukusu;
    //タイマーが起動しているか否か
    private boolean brightflag;

    private int delay = SensorManager.SENSOR_DELAY_NORMAL;
    private int type = Sensor.TYPE_LIGHT;

    AsyncHttpRequest async=new AsyncHttpRequest(this);

    String result;
    Button stop;
    int rainflag;
    //ex) 3分= 3x60x1000 = 180000 msec
    long countNumber = brightTime*1000;
    // インターバル msec
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_sound);
        count = 0;
        period = 100;
        brightflag = false;

        infoView = findViewById(R.id.info_view);
        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(brightTime * 10 * period));

        timeView = findViewById(R.id.time_view);
        timeView.setText(getString(R.string.time_format, brightTime));

        tv = findViewById(R.id.messageTextView);
        bar = findViewById(R.id.progressBar);
        bar.setMax(100);
        bar.setProgress(0);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //設定時刻が来たらロック解除しないでアラーム画面表示するやつ
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//oncreateした瞬間に、ansycする（＝テキストヴゅーに天気情報表示）
        try {
            async.execute(new URL("https://weather.tsukumijima.net/api/forecast/city/440010"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
/*      stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/
        timerText = findViewById(R.id.timer);
        timerText.setText(dataFormat.format(brightTime * 10 * period));//時間の初期値セット
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart in" + Thread.currentThread());
        super.onStart();
    }


    public void onResume() {
        super.onResume();
// 明るさセンサ(TYPE_LIGHT)のリストを取得（　講義スライドより）
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_LIGHT);
// ひとつ以上見つかったら、最初のセンサを取得してリスナーに登録
//managerはSensorManager
        if (sensors.size() != 0) {
            Sensor sensor = sensors.get(0);
            manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);//情報通知頻度は普通
        }
        manager.registerListener(this, sensor, delay);
        timer.scheduleAtFixedRate(new TimerTask() {//20ms間隔で更新
            @Override
            public void run() {
                handler.post(PlaySoundActivity.this);
                //UIスレッドへRunnableを渡す→明るさが動的に更新
            }
        }, 0, 20);//0を基準として20ミリ秒単位でタスクを繰り返す
        Toast.makeText(getApplicationContext(), "アラーム！"+"結果(result)は"+result, Toast.LENGTH_LONG).show();
    }
    protected void onPause() {
        super.onPause();
// 一時停止の際にリスナー登録を解除
        manager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged");
    }
    @Override

    public void run() {
        infoView.setText(getString(R.string.info_format, rukusu));//明るさ

       /* result = tv.getText().toString();
        Log.d("MyApp","変数 i は「" +result+ "」");*/

        //        Thread.sleep(1000);
        result = tv.getText().toString();

        if (result.contains("雨")|result.contains("雷")|result.contains("雪")) {//悪天候の時
            rainflag=1;}
        else if(result.contains("曇")|result.contains("晴")){//好天候の時
            rainflag=0;
        }
        else{
            rainflag=2;}//gettextに1秒くらいラグがあるのでresultがないときは動的処理でなんもしないようにする


        if (mp == null&rainflag!=2) {

            //resのrawディレクトリにtest.mp3を置いてある
            if (rainflag == 1) {//雨or雷or雪の時
                mp = MediaPlayer.create(this, R.raw.pipipi);
            }
            if (rainflag == 0) {//曇or晴の時
                mp = MediaPlayer.create(this, R.raw.hato);
            }

            mp.setLooping(true);//ループ設定
            mp.start();
        }
        if(count >= brightTime * 10) {//タイマーが6秒を超えたら
            Toast.makeText(getApplicationContext(), "アラーム終了！", Toast.LENGTH_LONG).show();
            //目覚ましアラーム消去
            PreferenceUtil pref = new PreferenceUtil(PlaySoundActivity.this);
            pref.delete(MainActivity.ALARM_TIME);

            //設定を初期化する
            initSetting();

            //300ms停止
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Toast.makeText(this, "take an error!!", Toast.LENGTH_LONG).show();
            }
            if (mp != null) {//アラーム音消去
                mp.stop();
                mp.release();
            }
            //設定画面に移る

            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        rukusu=event.values[0];

//プログレスバーを更新する部分
        if(rukusu > 300) {
            bar.setProgress(100);
        } else if(rukusu > 100) {
            bar.setProgress((int)(rukusu/3));
        } else {
            bar.setProgress((int)(0));
        }
//明るさ維持できないとタイマーリセットする部分
        if(rukusu >= 300 && !brightflag) {
            timerhandler.post(runnable);//6秒のタイマースタート
            brightflag = true;
        } else if(rukusu < 100 && brightflag) {//暗すぎると
            timerhandler.removeCallbacks(runnable);//タイマー破棄→リセット
            timerText.setText(dataFormat.format(brightTime * 10 * period));
            count = 0;
            brightflag = false;
        }
    }


    public void initSetting() {
        //センサーの停止
        manager.unregisterListener(this);

        //各フィールド変数を初期化
        count = 0;
        rukusu = 0;
        brightflag = false;

        //タイマーを初期化

        timerhandler.removeCallbacks(runnable);//タイマー終了
        timerText.setText(dataFormat.format(0));
    }



}
