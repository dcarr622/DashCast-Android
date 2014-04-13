package com.dashcast.app.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 4/13/14.
 */
public class Messenger {

    private static String TAG = "MESSENGER";
    public enum Event {CONNECTED_TO_WIFI, GOT_TEXT_MESSAGE, ALARM_SET}

    public static void sendMessage(Event e, String m) {
        SendMessageTask msg = new SendMessageTask();
        String endpoint = null;
        String message = null;
        if (e.equals(Event.ALARM_SET)) {
            endpoint =
            message = "morning";
        }
        if (e.equals(Event.CONNECTED_TO_WIFI)) {
            endpoint =
            message = "night";
        }
        if (e.equals(Event.GOT_TEXT_MESSAGE)) {
            endpoint =
            message = m;
        }
        msg.execute(endpoint, message);
    }

    private static class SendMessageTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String endpoint = strings[0];
            String message = strings[1];
            HttpPost postRequest = new HttpPost(endpoint);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("message", message));

            HttpResponse response = null;
            HttpEntity entity = null;

            DefaultHttpClient httpClient = new DefaultHttpClient();

            try {
                postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
