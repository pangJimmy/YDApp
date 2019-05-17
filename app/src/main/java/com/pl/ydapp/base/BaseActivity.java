package com.pl.ydapp.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.pl.ydapp.R;

/**
 * 基础类，提供公用接口，设置标题，弹窗，提示
 */
public class BaseActivity extends Activity {

    public Toolbar toolbar ;
    private TextView tvTitle ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.tool_bar) ;
        tvTitle = (TextView) findViewById(R.id.tv_title) ;
        //返回上一级
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ////////测试////////
        setBackBtnVisiable() ;
    }



    //设置标题栏返回按键可见
    public void setBackBtnVisiable(){
        toolbar.setNavigationIcon(R.drawable.ic_back);
    }


    //设置标题
    public void setToolbarTitle(String title){
        tvTitle.setText(title);
    }

    //设置标题，R.string.xx
    public void setToolbarTitle(int resID){
        tvTitle.setText(resID);
    }
}
