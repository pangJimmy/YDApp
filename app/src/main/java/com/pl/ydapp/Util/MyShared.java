package com.pl.ydapp.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于保存IP地址，用记名等
 */
public class MyShared {

    /**
     * 保存IP地址
     * @param context
     * @param ip
     */
    public void saveIP(Context context, String ip){
        SharedPreferences shared = context.getSharedPreferences("myshared", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("ip", ip) ;
        editor.commit() ;
    }

    /**
     * 获取 ip
     * @param context
     * @return
     */
    public String getIP(Context context){
        String ip ;
        SharedPreferences shared = context.getSharedPreferences("myshared", Context.MODE_PRIVATE) ;
        ip = shared.getString("ip","http://47.94.10.107:3388") ;
        return ip ;
    }


    /**
     * 保存token
     * @param context
     * @param token
     */
    public void saveToken(Context context, String token){
        SharedPreferences shared = context.getSharedPreferences("myshared", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("token", token) ;
        editor.commit() ;
    }

    /**
     * 获取 token
     * @param context
     * @return
     */
    public String getToken(Context context){
        String ip ;
        SharedPreferences shared = context.getSharedPreferences("myshared", Context.MODE_PRIVATE) ;
        ip = shared.getString("token","") ;
        return ip ;
    }

    /**
     * 保存token
     * @param context
     * @param token
     */
    public void saveUser(Context context, String token){
        SharedPreferences shared = context.getSharedPreferences("myshared", Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = shared.edit() ;
        editor.putString("user", token) ;
        editor.commit() ;
    }

    /**
     * 获取 token
     * @param context
     * @return
     */
    public String getUser(Context context){
        String ip ;
        SharedPreferences shared = context.getSharedPreferences("myshared", Context.MODE_PRIVATE) ;
        ip = shared.getString("user","") ;
        return ip ;
    }

}
