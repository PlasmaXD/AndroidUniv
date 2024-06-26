
package com.example.alarm.util;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    private static final String TAG = PreferenceUtil.class.getSimpleName();

    // 設定ファイル名
    private static final String CONFIG_FILE_NAME = "alarm_settings";

    private SharedPreferences mPreference;

    public PreferenceUtil(Context context) {
        mPreference = context.getSharedPreferences(PreferenceUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = mPreference.edit();
        //カキこむやつ
        editor.putLong(key, value);
        editor.apply();//保存
    }
    //キーに対応する値を返す
    public long getLong(String key) {
        return mPreference.getLong(key, 0);
    }
    //設定が存在しない場合は０
    public void delete(String key) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.remove(key);
        editor.apply();
    }


}