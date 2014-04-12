package com.dashcast.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
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

        mMediaRouter = MediaRouter.getInstance(context);

        MediaRouter.RouteInfo chromecastRoute = mMediaRouter.getRoutes().get(0);
        if (chromecastRoute.isEnabled()) {
            Log.d(TAG, "found ChromeCast to send alarm message to");
            CastDevice myChromeCast = CastDevice.getFromBundle(chromecastRoute.getExtras());
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(myChromeCast, null);
            mApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            if (setMessage && mApiClient != null) {
                                Cast.CastApi.sendMessage(mApiClient, DashCastChannel.NAMESPACE, nextAlarm);
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .build();

            mApiClient.connect();
        }


    }

}
