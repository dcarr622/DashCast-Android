package com.dashcast.app.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;

import com.dashcast.app.util.Messenger;

public class TextReciever extends BroadcastReceiver {
    public TextReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];
        // Create messages for each incoming PDU
        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }
        for (SmsMessage msg : sms) {
            String no = msg.getOriginatingAddress();
            //Resolving the contact name from the contacts.
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(no));
            Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
            try {
                c.moveToFirst();
                String displayName = c.getString(0);
                String ContactName = displayName;
//                Toast.makeText(context, ContactName + " " + msg.getMessageBody(), Toast.LENGTH_LONG).show();
                Messenger.sendMessage(Messenger.Event.GOT_TEXT_MESSAGE, ContactName + " : " + msg.getMessageBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

