package com.dashcast.app;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;

/**
 * Created by david on 4/12/14.
 */
public class DashCastChannel implements Cast.MessageReceivedCallback {

    public static final String NAMESPACE = "urn:x-cast:com.qcastapp";

    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {

    }
}
