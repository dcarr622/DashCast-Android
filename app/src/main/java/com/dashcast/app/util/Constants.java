package com.dashcast.app.util;

import com.google.android.gms.common.Scopes;

/**
 * Created by david on 4/12/14.
 */
public class Constants {

//    public static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/calendar.readonly https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me";
//      public static final String SCOPE = "oauth2:server:client_id:99312021964-5hc9j067l4svgh87sg3vc8ran4m1ctbm.apps.googleusercontent.com:api_scope:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/calendar.readonly https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me";
    public static final String clientID = "99312021964-5hc9j067l4svgh87sg3vc8ran4m1ctbm.apps.googleusercontent.com";
    public static final String scopesString = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME + " " + Scopes.PROFILE;
    public static final String SCOPE = "oauth2:server:client_id:" + clientID + ":api_scope:" + scopesString;

    public static final String CAST_APP_ID = "D5A2EB6C";
    public static final String WIDGETS_ENDPOINT = "http://107.170.192.218/widget/all";

    public enum FormInput {STRING};

}
