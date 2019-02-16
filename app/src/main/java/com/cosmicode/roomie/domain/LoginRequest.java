package com.cosmicode.roomie.domain;

public class LoginRequest {

    private String password;
    private String username;

    public LoginRequest(){
        //Needed for jackson
    }
    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
