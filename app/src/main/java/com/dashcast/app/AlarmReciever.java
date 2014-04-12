package com.dashcast.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by david on 4/12/14.
 */
public class AlarmReciever extends BroadcastReceiver {

    private static final String TAG = "AlarmReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "intent=" + intent);
        Boolean message = intent.getBooleanExtra("alarmSet",false);
        Log.d(TAG, "alarmSet: " + message);
        Log.d(TAG, "next alarm: " + Settings.System.getString(context.getContentResolver(),android.provider.Settings.System.NEXT_ALARM_FORMATTED));
    }
}
