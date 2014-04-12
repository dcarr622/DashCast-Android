package com.dashcast.app.activities;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.widget.Toast;

import com.dashcast.app.Constants;
import com.dashcast.app.R;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks {

    private static final int ACCOUNT_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_AUTHORIZATION = 2;

    private static final String TAG = "MainActivity";
    /*Shared prefs*/
    private static SharedPreferences sharedPrefs;
    private Context mContext;
    private static SocialAuthAdapter mSocialAuthAdapter;

    /* Chromecast Stuff */
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MyMediaRouterCallback mMediaRouterCallback;
    private DashCastChannel mChannel;
    private GoogleApiClient mApiClient;

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

        /* Set up Cast button */
        MediaRouteButton mediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);

        mMediaRouter = MediaRouter.getInstance(this);

        mMediaRouteSelector = new MediaRouteSelector.Builder()
                //.addControlCategory(CastMediaControlIntent.categoryForRemotePlayback(Constants.CAST_APP_ID))
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build();

        mMediaRouterCallback = new MyMediaRouterCallback();

        mediaRouteButton.setRouteSelector(mMediaRouteSelector);

        mSocialAuthAdapter = new SocialAuthAdapter(new ResponseListener());

//        mSocialAuthAdapter.authorize(this, SocialAuthAdapter.Provider.FACEBOOK);
//        Log.d(TAG, mSocialAuthAdapter.getCurrentProvider().getAccessGrant().getKey());

        final String account = sharedPrefs.getString("account", null);
        if (account == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, ACCOUNT_PICK_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onPause();
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

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Cast.CastApi.launchApplication(mApiClient, Constants.CAST_APP_ID).setResultCallback(new ResultCallback<Cast.ApplicationConnectionResult>() {
                @Override
                public void onResult(Cast.ApplicationConnectionResult applicationConnectionResult) {
                    mChannel = new DashCastChannel();
                    try {
                        Cast.CastApi.setMessageReceivedCallbacks(mApiClient, DashCastChannel.NAMESPACE, mChannel);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to send message", e);
        }
        Log.d(TAG, "connected to chromecast");
    }

    @Override
    public void onConnectionSuspended(int i) {

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

    private class MyMediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            Toast.makeText(MainActivity.this, info.getName(), Toast.LENGTH_LONG).show();
            launchReceiver(CastDevice.getFromBundle(info.getExtras()));
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
        }
    }

    private void launchReceiver(CastDevice device) {
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(device, null);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(this)
                .build();

        mApiClient.connect();
    }

    private class DashCastChannel implements Cast.MessageReceivedCallback {

        public static final String NAMESPACE = "urn:x-cast:com.qcastapp";

        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {

        }
    }

}
