package com.dashcast.app.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.dashcast.app.Constants;
import com.dashcast.app.R;

import org.brickred.socialauth.android.SocialAuthAdapter;

import java.io.IOException;


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
        /*Get shared prefs*/
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mContext = this;
        /*SocialAuth*/
        mSocialAuthAdapter = MainActivity.getSocialAuthAdapter();

        findViewById(R.id.google_plus_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "gPlus clicked");
                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE},
                        true, null, null, null, null);
                startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
            }
        });

        findViewById(R.id.facebook_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocialAuthAdapter.setTitleVisible(false);
                mSocialAuthAdapter.authorize(LoginActivity.this, SocialAuthAdapter.Provider.FACEBOOK);
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        if (requestCode == ACCOUNT_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                ImageView gPlusCheck = (ImageView) findViewById(R.id.check_box_gplus);
                gPlusCheck.setVisibility(View.VISIBLE);
                findViewById(R.id.google_plus_sign_in_button).setOnClickListener(null);

                Button next = (Button) findViewById(R.id.next_button);
                next.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_offwhite));
                next.setTextColor(Color.BLACK);
                setNextListener();

                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                sharedPrefs.edit().putString("account", accountName).commit();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Log.d("scope", Constants.SCOPE);
                            Log.d("token", GoogleAuthUtil.getToken(LoginActivity.this, sharedPrefs.getString("account", null), Constants.SCOPE));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (UserRecoverableAuthException e) {
                            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                        } catch (GoogleAuthException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();

            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void setNextListener() {
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(mContext, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

}
