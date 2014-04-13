package com.dashcast.app.recievers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.dashcast.app.util.Messenger;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        final Boolean setMessage = intent.getBooleanExtra("alarmSet", false);
        Log.d(TAG, "alarmSet: " + setMessage);
        final String nextAlarm = Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.NEXT_ALARM_FORMATTED);
        Log.d(TAG, "next alarm: " + nextAlarm);
        if (setMessage) {
            long realAlarmTime = getRealTime(context, nextAlarm);
            scheduleAlarmMessage(context, Messenger.Event.ALARM_SET, realAlarmTime);
//            Messenger.sendMessage(context, Messenger.Event.ALARM_SET, "alarm");
        }

    }

    private long getRealTime(Context context, String nextAlarm) {
        String format = android.text.format.DateFormat.is24HourFormat(context) ? "E k:mm" : "E h:mm aa";
        Calendar nextAlarmCal = Calendar.getInstance();
        Calendar nextAlarmIncomplete = Calendar.getInstance();
        try {
            nextAlarmIncomplete.setTime(new SimpleDateFormat(format).parse(nextAlarm));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // replace valid fields of the current time with what we got in nextAlarm
        int[] fieldsToCopy = {Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.DAY_OF_WEEK};
        for (int field : fieldsToCopy) {
            nextAlarmCal.set(field, nextAlarmIncomplete.get(field));
        }
        nextAlarmCal.set(Calendar.SECOND, 0);

        // if the alarm is next week we have wrong date now (in the past). Adding 7 days should fix this
        if (nextAlarmCal.before(Calendar.getInstance())) {
            nextAlarmCal.add(Calendar.DATE, 7);
        }
        Log.d(TAG, String.valueOf(nextAlarmCal.getTime()));
        return nextAlarmCal.getTimeInMillis();

    }

    private void scheduleAlarmMessage(Context context, Messenger.Event event, long nextAlarm) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, OnAlarmReceive.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, nextAlarm, pendingIntent);
        Log.d(TAG, "Setup the alarm");
    }

}
