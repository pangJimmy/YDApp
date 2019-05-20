package com.pl.ydapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pl.ydapp.Util.Logger;
import com.pl.ydapp.Util.MyShared;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.entity.LoginResult;
import com.pl.ydapp.httpserver.HttpConstant;
import com.pl.ydapp.httpserver.HttpServer;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONException;
import org.json.JSONObject;

//登陆界面
public class LoginActivity extends BaseActivity implements View.OnClickListener{


    private EditText editUser ;
    private EditText editPwd ;
    private Button btnLogin ;
    private ImageButton imgBtnSettings ;

    private String user ;
    private String pwd ;
    private Context context ;
    private Handler handler = new Handler() ;
    //登陆结果
    private LoginResult result ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);

        setToolbarTitle(R.string.login);
        context = this ;
        initView() ;
    }

    private void initView() {

        editUser = findViewById(R.id.editText_user) ;
        editPwd = findViewById(R.id.editText_pwd) ;
        imgBtnSettings = findViewById(R.id.img_settings) ;
        btnLogin = findViewById(R.id.button_login) ;

        imgBtnSettings.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }


    //登陆线程
    private Runnable loginTask = new Runnable() {
        @Override
        public void run() {
            HttpServer httpServer = new HttpServer(context) ;
            result = httpServer.login(user ,pwd) ;
            handleLoginResult() ;
            dismiss() ;
        }
    } ;

    //处理登陆结果
    private void handleLoginResult(){

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(result.success && result.code == HttpConstant.REQUEST_OK && result.data != null){
                        if(tipDialog != null) {
                            tipDialog.dismiss();
                        }
                        String token = result.data.token ;
                        if(token != null){
                            //保存token
                            new MyShared().saveToken(context, token);
                            Logger.e("test" , token);
                            //跳到主界面
                            Intent intent = new Intent(context, Main2Activity.class) ;
                            startActivity(intent);
                            finish();
                        }else{
                            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, result.message);
                        }

                    }else{
                        //给出相应的提示
                        if(result.message != null && result.message.length() > 0){
                            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, result.message);
                        }else{
                            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, R.string.unknow_network_err);
                        }
                    }
                    dismiss();
                }
            }) ;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login://登陆
                login() ;
                break ;
            case R.id.img_settings://设置IP
                Intent intent = new Intent(this, SettingsActivity.class) ;
                startActivity(intent);
                break ;

        }
    }

    //登陆
    private void login(){
        user = editUser.getText().toString().trim() ;
        pwd = editPwd.getText().toString().trim() ;
        if(user == null || user.length() == 0){
            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_NOTHING, R.string.user_null);
            dismiss();
            return;
        }
        if(pwd == null || pwd.length() == 0){
            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_NOTHING, R.string.passwd_null);
            dismiss() ;
            return;
        }
        showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.logining);
        new Thread(loginTask).start();
    }

    private void dismiss(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(tipDialog != null) {
                    tipDialog.dismiss();
                }
            }
        }, 1500) ;
    }


}
