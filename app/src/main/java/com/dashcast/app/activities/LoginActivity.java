package com.dashcast.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dashcast.app.R;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class LoginActivity extends Activity {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;
    private static final String TAG = "LoginActivity";
    /*Shared prefs*/
    private static SharedPreferences sharedPrefs;
    private Context mContext;
    private static SocialAuthAdapter mSocialAuthAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Sorry :( */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /*Get shared prefs*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mContext = this;
        /*SocialAuth*/
        mSocialAuthAdapter = MainActivity.getSocialAuthAdapter();

        findViewById(R.id.google_plus_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String googleAuthURL = "http://dash.ptzlabs.com/user";
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final AlertDialog alert = builder.create();
                WebView googleWebView = new WebView(mContext){
                    @Override
                    public boolean onCheckIsTextEditor() {
                        return true;
                    }
                };
                googleWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        boolean isRedirect = url.indexOf("http://dash.ptzlabs.com/user/oAuthCallback") == 0;
                        Log.d(TAG, url);
                        if (isRedirect) {
//                            Log.v(TAG, "redirect detected");
                            URL aURL = null;
                            try {
                                aURL = new URL(url);
                                URLConnection conn = aURL.openConnection();
                                conn.connect();
                                InputStream is = conn.getInputStream();

                                BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                                StringBuilder responseStrBuilder = new StringBuilder();
                                String inputStr;
                                while ((inputStr = streamReader.readLine()) != null)
                                    responseStrBuilder.append(inputStr);
//                                Log.d(TAG, responseStrBuilder.toString());
                                JSONObject userJSON = new JSONObject(responseStrBuilder.toString());
                                Log.d(TAG, userJSON.getString("userId"));
                                sharedPrefs.edit().putString("userId", userJSON.getString("userId")).commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            alert.dismiss();
                            ImageView gPlusCheck = (ImageView) findViewById(R.id.check_box_gplus);
                            gPlusCheck.setVisibility(View.VISIBLE);
                            Intent mainIntent = new Intent(mContext, MainActivity.class);
                            startActivity(mainIntent);
                            return true;
                        }
                        return false;
                    }
                });
                googleWebView.loadUrl(googleAuthURL.toString());
                googleWebView.getSettings().setJavaScriptEnabled(true);
                googleWebView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
                alert.setView(googleWebView);
                alert.show();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

}
