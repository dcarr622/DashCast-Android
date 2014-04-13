package com.dashcast.app.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dashcast.app.util.Messenger;

/**
 * Created by david on 4/13/14.
 */

public class OnAlarmReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Messenger.sendMessage(context, Messenger.Event.ALARM_SET, "alarm");
    }
}