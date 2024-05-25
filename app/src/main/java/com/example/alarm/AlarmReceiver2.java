package com.example.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//光目覚まし用
public class AlarmReceiver2 extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver2.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startActivityIntent = new Intent(context, PlayLightActivity.class);
        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
    }
}
