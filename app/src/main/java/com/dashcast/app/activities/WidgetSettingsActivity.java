package com.dashcast.app.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dashcast.app.Constants;
import com.dashcast.app.R;

import java.util.HashMap;
import java.util.Map;

public class WidgetSettingsActivity extends Activity {

    HashMap<String, Constants.FormInput> configFields;
    Map<String, EditText> formFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_settings);

        formFields = new HashMap<String, EditText>();

        LinearLayout formFieldsLayout = (LinearLayout) findViewById(R.id.form_fields);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            configFields = (HashMap<String, Constants.FormInput>) bundle.getSerializable("fields");
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

            }
        });
    }




}
