package com.dashcast.app.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dashcast.app.Constants;
import com.dashcast.app.R;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    private static final String TAG = "MainActivity";
    /*Shared prefs*/
    private static SharedPreferences sharedPrefs;
    private Context mContext;
    private static SocialAuthAdapter mSocialAuthAdapter;

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

        mSocialAuthAdapter = new SocialAuthAdapter(new ResponseListener());;

//        mSocialAuthAdapter.authorize(this, SocialAuthAdapter.Provider.FACEBOOK);
//        Log.d(TAG, mSocialAuthAdapter.getCurrentProvider().getAccessGrant().getKey());

        final String account = sharedPrefs.getString("account", null);
//        if (account == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACCOUNT_PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getPreferences(MODE_PRIVATE).edit().putString("account", accountName).commit();

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Log.d("scope", Constants.SCOPE);
                            Log.d("token", GoogleAuthUtil.getToken(MainActivity.this, accountName, Constants.SCOPE));
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

}
