package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.icrisat.sbdm.ismu.retrofit.gobii.metadata.Metadata;

public class Token {
    private Metadata metadata;
    private String access_token;
    private String expires_in;
    private String userDisplayName;

    public String getAccess_token() {
        return access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }
}
