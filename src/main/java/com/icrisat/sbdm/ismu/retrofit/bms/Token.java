package com.icrisat.sbdm.ismu.retrofit.bms;

class Token {
    private String userDisplayName;
    private String access_token;
    private long expires_in;

    String getAccess_token() {
        return access_token;
    }
}
