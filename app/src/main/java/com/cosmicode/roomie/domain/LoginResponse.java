package com.cosmicode.roomie.domain;

public class LoginResponse {
    public String id_token;

    public LoginResponse(){

    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
}
