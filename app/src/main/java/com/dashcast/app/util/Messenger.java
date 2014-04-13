package com.dashcast.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by david on 4/13/14.
 */
public class Messenger {

    private static String TAG = "MESSENGER";
    private static SharedPreferences sharedPrefs;
    public enum Event {CONNECTED_TO_WIFI, GOT_TEXT_MESSAGE, ALARM_SET}
    private static Context mContext;
    private static final String endpoint = "http://107.170.192.218/mode";

//    /mode?user=<userid>&mode=<mode>

    public static void sendMessage(Context c, Event e, String m) {
        mContext = c;
        SendMessageTask msg = new SendMessageTask();
        String mode = null;
        if (e.equals(Event.ALARM_SET)) {
            mode = "day";
        }
        if (e.equals(Event.CONNECTED_TO_WIFI)) {
            mode = "night";
        }
//        if (e.equals(Event.GOT_TEXT_MESSAGE)) {
//            endpoint =
//            message = m;
//        }
        msg.execute(mode);
    }

    private static class SendMessageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String mode = strings[0];

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String userId = sharedPrefs.getString("userId", "jabroni");

            String postURL = endpoint + "?user=" + userId + "&mode=" + mode;

            HttpPost postRequest = new HttpPost(postURL);


            Log.d(TAG, userId);

//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("user", userId));
//            nameValuePairs.add(new BasicNameValuePair("mode", mode));

            Log.d(TAG, mode);

            Log.d(TAG, postURL);

            HttpResponse response = null;
            HttpEntity entity = null;

            DefaultHttpClient httpClient = new DefaultHttpClient();

            try {
//                postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(postRequest);
                entity = response.getEntity();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (entity != null) {
                Log.d(TAG, "entity notnull");
                String retSrc = null;
                String status = null;
                try {
                    retSrc = EntityUtils.toString(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.d(TAG, "success");
            } else {
                Log.d(TAG, "error posting message");
            }
        }

    }
}
