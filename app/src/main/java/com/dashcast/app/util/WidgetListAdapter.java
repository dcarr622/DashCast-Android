package com.dashcast.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dashcast.app.R;
import com.dashcast.app.activities.MainActivity;
import com.dashcast.app.activities.WidgetSettingsActivity;
import com.squareup.picasso.Picasso;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by david on 4/12/14.
 */
public class WidgetListAdapter extends BaseAdapter implements ListAdapter {

    private static final String TAG = "SongListAdapter";
    private Activity mContext;
    private final JSONArray jsonArray;

    public WidgetListAdapter(Activity c, JSONArray jsonArray) {
        mContext = c;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        try {
            return jsonArray.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return jsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final JSONObject thisWidget = (JSONObject) getItem(i);
        String widgetTitle = null;
        String imageURL = null;
        try {
            widgetTitle = thisWidget.getString("name");
            imageURL = thisWidget.getString("logo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.widget_list_item, null);
        TextView widgetTitleView = (TextView) view.findViewById(R.id.list_widget_name);
        ImageView widgetIconView = (ImageView) view.findViewById(R.id.widget_icon);
        CheckBox enableCB = (CheckBox) view.findViewById(R.id.enable_checkbox);
        enableCB.setTag(new Integer(i));
        enableCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "checkbox clicked");
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    enableOrSetup((Integer) view.getTag());
                }
            }
        });
        try {
            widgetTitleView.setText(widgetTitle);
            Picasso.with(mContext).load(imageURL).into(widgetIconView);
            Log.d(TAG, thisWidget.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void enableOrSetup(Integer i) {
//        Log.d(TAG, String.valueOf(i));
        final JSONObject thisWidget = (JSONObject) getItem(i);
        String widgetTitle = null;
        try {
            widgetTitle = thisWidget.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (widgetTitle.contains("Facebook")) {
            SocialAuthAdapter mSocialAuthAdapter = MainActivity.getSocialAuthAdapter();
            mSocialAuthAdapter.setTitleVisible(false);
            mSocialAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.FACEBOOK);
        }

        JSONObject data = null;
        try {
            data = thisWidget.getJSONObject("data");
//            Log.d(TAG, data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (data != null) {
            HashMap<String, Constants.FormInput> configFields = new HashMap<String, Constants.FormInput>();
            Iterator<String> iter = data.keys();
            while(iter.hasNext()){
                try {
                    String s = iter.next();
                    configFields.put(s, Constants.FormInput.valueOf(data.getString(s)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (configFields.size() > 0) {
                Intent widgetSettingIntent = new Intent(mContext, WidgetSettingsActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable("fields", configFields);
                extras.putString("widget", widgetTitle);
                widgetSettingIntent.putExtras(extras);
                mContext.startActivity(widgetSettingIntent);

            }
        }
    }
}
