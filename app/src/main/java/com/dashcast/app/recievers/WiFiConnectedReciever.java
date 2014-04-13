package com.dashcast.app.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.dashcast.app.util.Messenger;

public class WiFiConnectedReciever extends BroadcastReceiver {

    private static SharedPreferences sharedPrefs;
    private static Context mContext;
    private static final String TAG = "WiFiConnectedReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // Wifi is connected
                Log.d("Inetify", "Wifi is connected: " + String.valueOf(networkInfo));
                if (checkIfAtHome()) {
//                    Log.d(TAG, "You are at home!");
                    Messenger.sendMessage(Messenger.Event.CONNECTED_TO_WIFI, "home");
                }
            }
        }
    }

    /** Detect you are connected to home WiFi network. */
    private boolean checkIfAtHome() {
        boolean connected = false;

//        String homeSSID = sharedPrefs.getString("homeSSID", "none");
        String homeSSID = "MuchConnect";
        if (!(homeSSID.equals("none"))) {
            WifiManager wifiManager =
                    (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifi = wifiManager.getConnectionInfo();
            if (wifi != null) {
                // get current router Mac address
                String ssid = wifi.getSSID();
                connected = homeSSID.equals(ssid);
            }
        }

        return connected;
    }
}
