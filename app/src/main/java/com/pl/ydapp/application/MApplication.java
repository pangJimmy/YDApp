package com.pl.ydapp.application;

import android.app.Application;

import com.pl.ydapp.entity.LoginResult;

public class MApplication extends Application{

    //登陆信息
    private LoginResult loginResult ;

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
