package com.dashcast.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dashcast.app.activities.MainActivity;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        try {
            widgetTitle = thisWidget.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.widget_list_item, null);
        TextView widgetTitleView = (TextView) view.findViewById(R.id.list_widget_name);
        CheckBox enableCB = (CheckBox) view.findViewById(R.id.enable_checkbox);
        enableCB.setTag(new Integer(i));
        enableCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "checkbox clicked");
                CheckBox checkBox = (CheckBox)view;
                if(checkBox.isChecked()){
                    enableOrSetup((Integer) view.getTag());
                }
            }
        });
        try {
            widgetTitleView.setText(widgetTitle);
            Log.d(TAG, thisWidget.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void enableOrSetup(Integer i) {
        Log.d(TAG, String.valueOf(i));
        final JSONObject thisWidget = (JSONObject) getItem(i);
        String widgetTitle = null;
        try {
            widgetTitle = thisWidget.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (widgetTitle.contains("Traffic")) {

        }

        if (widgetTitle.contains("Test")) {
            SocialAuthAdapter mSocialAuthAdapter = MainActivity.getSocialAuthAdapter();
            mSocialAuthAdapter.setTitleVisible(false);
            mSocialAuthAdapter.authorize(mContext, SocialAuthAdapter.Provider.FACEBOOK);
        }
    }
}
