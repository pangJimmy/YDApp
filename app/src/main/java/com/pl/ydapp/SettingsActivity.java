package com.pl.ydapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pl.ydapp.Util.MyShared;
import com.pl.ydapp.base.BaseActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

//设置IP
public class SettingsActivity extends BaseActivity implements View.OnClickListener{

    private EditText editIP ;
    private Button btnSave ;

    private MyShared myShared ;
    private Context context ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);
        setBackBtnVisiable();
        myShared = new MyShared() ;
        context = this ;
        initView() ;
    }

    private void initView() {
        editIP = findViewById(R.id.editText_ip) ;
        editIP.setText(myShared.getIP(context));
        btnSave = findViewById(R.id.button_save) ;
        btnSave.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_save:
                save();
                break ;
        }
    }

    private void save(){
        String ip = editIP.getText().toString().trim() ;
        if(ip != null){
            myShared.saveIP(context, ip);
            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_SUCCESS, R.string.save_success);
            //延时1.5秒后返回
            editIP.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(tipDialog != null){
                        tipDialog.dismiss();
                    }
                    finish();
                }
            }, 1500);
        }else{
            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, R.string.ip_null);
            //延时1.5秒后
            editIP.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(tipDialog != null){
                        tipDialog.dismiss();
                    }

                }
            }, 1500);
        }
    }
}
