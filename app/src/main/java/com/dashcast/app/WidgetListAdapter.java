package com.dashcast.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dashcast.app.R;

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
        JSONObject thisWidget = (JSONObject) getItem(i);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.widget_list_item, null);
        TextView widgetTitle = (TextView) view.findViewById(R.id.list_widget_name);
        try {
            widgetTitle.setText(thisWidget.getString("name"));
            Log.d(TAG, thisWidget.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
