package com.example.alarm;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;


import com.example.alarm.util.PreferenceUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    public static final String ALARM_TIME = "alarm_time";

    Button button;

    Button button2;
    Switch alarmSwitch;
    Switch alarmSwitch2;

    Calendar alarmCalendar = Calendar.getInstance();
    PreferenceUtil pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new PreferenceUtil(this);
        setupViews();
        setListeners();
    }

    private void setupViews() {
        button = (Button) findViewById(R.id.button);
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        button2 = (Button) findViewById(R.id.button2);
        alarmSwitch2 = (Switch) findViewById(R.id.alarm_switch2);

        long alarmTime = pref.getLong(ALARM_TIME);//ALARM_TIMEがキーの時刻を取得して↓でせっと
        if (alarmTime != 0) {//時刻がセットされているなら（されていないときは０）
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//セットする時刻の書式の設定
            Date date = new Date(alarmTime);
            button.setText(df.format(date));
            button2.setText(df.format(date));
            alarmSwitch.setChecked(false);
            alarmSwitch2.setChecked(false);
        }
    }

    private void setListeners() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int monthOfYear = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, final int y, final int m, final int d) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                alarmCalendar.set(Calendar.YEAR, y);
                                alarmCalendar.set(Calendar.MONTH, m);
                                alarmCalendar.set(Calendar.DAY_OF_MONTH, d);
                                alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alarmCalendar.set(Calendar.MINUTE, minute);
                                alarmCalendar.set(Calendar.SECOND, 0);
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                button.setText(df.format(alarmCalendar.getTime()));

                            }
                        }, hour, minute, true);
                        timePickerDialog.show();
                    }
                }, year, monthOfYear, dayOfMonth);
                datePickerDialog.show();


            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int monthOfYear = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, final int y, final int m, final int d) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {//年月日、何時何分かをセット
                                alarmCalendar.set(Calendar.YEAR, y);
                                alarmCalendar.set(Calendar.MONTH, m);
                                alarmCalendar.set(Calendar.DAY_OF_MONTH, d);
                                alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                alarmCalendar.set(Calendar.MINUTE, minute);
                                alarmCalendar.set(Calendar.SECOND, 0);
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                button2.setText(df.format(alarmCalendar.getTime()));

                            }
                        }, hour, minute, true);
                        timePickerDialog.show();
                    }
                }, year, monthOfYear, dayOfMonth);
                datePickerDialog.show();


            }
        });

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    register(alarmCalendar.getTimeInMillis(),1);
                } else {
                    unregister(1);
                }
            }
        });
        alarmSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    register(alarmCalendar.getTimeInMillis(),2);
                } else {
                    unregister(2);
                }
            }
        });
    }

    // 登録
    private void register(long alarmTimeMillis,int flag) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(flag);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTimeMillis, null), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
        }
        // 保存
        pref.setLong(ALARM_TIME, alarmTimeMillis);//alarmTimeMillisをキーALARM_TIMEで保存
    }

    // 解除
    private void unregister(int flag) {//alarmManagerとprefを破棄
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(flag));//
        pref.delete(ALARM_TIME);
    }

    private PendingIntent getPendingIntent(int flag) {
        Intent intent = null;//new Intent(this, AlarmReceiver.class);
        int requestcode = 0;
        if(flag==1) {//上のアラームのとき
            intent = new Intent(this, AlarmReceiver.class);
            intent.setClass(this, AlarmReceiver.class);
            requestcode=0;
        }
        if(flag==2){//下のアラームの時
            intent = new Intent(this, AlarmReceiver2.class);
            intent.setClass(this, AlarmReceiver2.class);
            requestcode=1;
        }



        return PendingIntent.getBroadcast(this, requestcode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}

