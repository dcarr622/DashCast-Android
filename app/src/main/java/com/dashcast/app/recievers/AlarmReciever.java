package com.dashcast.app.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.dashcast.app.util.Messenger;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by david on 4/12/14.
 */
public class AlarmReciever extends BroadcastReceiver {

    private static final String TAG = "AlarmReciever";
    private GoogleApiClient mApiClient;
    private MediaRouter mMediaRouter;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "intent=" + intent);
        final Boolean setMessage = intent.getBooleanExtra("alarmSet",false);
        Log.d(TAG, "alarmSet: " + setMessage);
        final String nextAlarm = Settings.System.getString(context.getContentResolver(),android.provider.Settings.System.NEXT_ALARM_FORMATTED);
        Log.d(TAG, "next alarm: " + nextAlarm);

        Messenger.sendMessage(Messenger.Event.ALARM_SET, nextAlarm);

    }

}
