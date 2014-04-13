package com.dashcast.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dashcast.app.R;
import com.dashcast.app.util.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetSettingsActivity extends Activity {

    HashMap<String, Constants.FormInput> configFields;
    Map<String, EditText> formFields;
    private String widgetName;
    private static SharedPreferences sharedPrefs;
    private static Context mContext;
    private static final String endpoint = "http://107.170.192.218/usage";
    private static final String TAG = "WidgetSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_settings);

        mContext = this;

        formFields = new HashMap<String, EditText>();

         /*Get shared prefs*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        final LinearLayout formFieldsLayout = (LinearLayout) findViewById(R.id.form_fields);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            configFields = (HashMap<String, Constants.FormInput>) bundle.getSerializable("fields");
            widgetName = bundle.getString("widget");
            for (String s : configFields.keySet()) {
                Log.d(s, configFields.get(s).toString());
                if (configFields.get(s).equals(Constants.FormInput.STRING)) {
                    EditText newEditText = (EditText) getLayoutInflater().inflate(R.layout.edit_text_style, null);
                    formFields.put(s, newEditText);
                }
            }
        }

        for (String s: formFields.keySet()) {
            EditText et = formFields.get(s);
            et.setHint(s);
            et.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_offwhite));
            formFieldsLayout.addView(et);
        }

        TextView submitButton = (TextView) findViewById(R.id.submit_button_child);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //build data
                JSONObject dataObject = new JSONObject();
                for (String s: formFields.keySet()) {
                    try {
                        dataObject.accumulate(s, formFields.get(s).getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SubmitWidgetSettingsTask submitTask = new SubmitWidgetSettingsTask();
                submitTask.execute(widgetName, dataObject.toString());
            }
        });
    }

    private static class SubmitWidgetSettingsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String widget = strings[0];
            String data = strings[1];

            Log.d(TAG, "widget: "+ widget);
            Log.d(TAG, "data: "+ data);

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String userId = sharedPrefs.getString("userId", "102190104458073909670");

            HttpPost postRequest = new HttpPost(endpoint);

            Log.d(TAG, userId);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("user", userId));
            nameValuePairs.add(new BasicNameValuePair("widgetId", widget));
            nameValuePairs.add(new BasicNameValuePair("data", data));

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
