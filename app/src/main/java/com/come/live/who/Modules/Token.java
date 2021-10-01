package com.come.live.who.Modules;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("token")
    String Token;

    public Token(String token) {
        Token = token;
    }

    public Token() {
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
