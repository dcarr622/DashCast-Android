package com.dashcast.app.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dashcast.app.Constants;
import com.dashcast.app.R;
import com.dashcast.app.WidgetListAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONArray;

public class MainActivity extends ListActivity {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    private static final String TAG = "MainActivity";
    /*Shared prefs*/
    private static SharedPreferences sharedPrefs;
    private Activity mContext;
    private static SocialAuthAdapter mSocialAuthAdapter;
    private static JSONArray widgetsArray;

    public static SocialAuthAdapter getSocialAuthAdapter() {
        return mSocialAuthAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         /*Get shared prefs*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mContext = this;

        mSocialAuthAdapter = new SocialAuthAdapter(new ResponseListener());

//        mSocialAuthAdapter.authorize(this, SocialAuthAdapter.Provider.FACEBOOK);
//        Log.d(TAG, mSocialAuthAdapter.getCurrentProvider().getAccessGrant().getKey());

        final String userId = sharedPrefs.getString("userId", null);
//        if (userId == null) {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
//        }

        new FetchWidgetsTask().execute();
    }

    private final class ResponseListener implements DialogListener {
        public void onComplete(Bundle values) {
            String providerName = values.getString(SocialAuthAdapter.PROVIDER);
//            boolean isSignedIn = sharedPrefs.getBoolean(getString(R.string.app_sign_in_completed), false);
            Log.d(TAG, providerName + " Complete");
            if (providerName.equals("facebook")) {
                Log.d(TAG, "Facebook now active, setting sharedpref to True");
//                sharedPrefs.edit().putBoolean(getString(R.string.facebook_authorized), true).commit();
//                mSocialAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.FACEBOOK);
                Log.d(TAG, "FBToken: " + mSocialAuthAdapter.getCurrentProvider().getAccessGrant().getKey());
            }
        }

        @Override
        public void onError(SocialAuthError socialAuthError) {
            Log.d(TAG, "Error");

        }

        public void onCancel() {
            Log.d(TAG, "Cancelled");
        }

        @Override
        public void onBack() {

        }

    }

    public class FetchWidgetsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            HttpPost postRequest = new HttpPost(Constants.WIDGETS_ENDPOINT);
            HttpResponse response = null;
            HttpEntity entity = null;
            DefaultHttpClient httpClient = new DefaultHttpClient();

            try {
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
                    widgetsArray = new JSONArray(retSrc); //Convert String to JSON Array
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                WidgetListAdapter widgets = null;
                widgets = new WidgetListAdapter(mContext, widgetsArray);
                if (widgets.getCount() == 0) {
                    Log.d(TAG, "No widgets found");
                }
                setListAdapter(widgets);
            } else {
                Log.d(TAG, "error getting widgets");
            }
        }

    }


}
