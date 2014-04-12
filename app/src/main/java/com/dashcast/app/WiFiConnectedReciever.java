package com.dashcast.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiConnectedReciever extends BroadcastReceiver {

    private static SharedPreferences sharedPrefs;
    private static Context mContext;
    private static final String TAG = "WiFiConnectedReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive in WiFiConnectedReciever");
        mContext = context;
        String action = intent.getAction();
        Log.d(TAG, action);
//        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (SupplicantState.isValidState(state)
                    && state == SupplicantState.COMPLETED) {

                boolean connected = checkIfAtHome();
            }
//        }
    }

    /** Detect you are connected to home WiFi network. */
    private boolean checkIfAtHome() {
        boolean connected = false;

        String homeSSID = sharedPrefs.getString("homeSSID", "none");
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
