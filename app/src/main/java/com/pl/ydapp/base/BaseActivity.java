package com.pl.ydapp.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.pl.ydapp.ComfirmOutActivity;
import com.pl.ydapp.R;
import com.pl.ydapp.scan.IScanResult;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

/**
 * 基础类，提供公用接口，设置标题，弹窗，提示
 */
public class BaseActivity extends Activity {

    public Toolbar toolbar ;
    private TextView tvTitle ;

    //扫描设备
    private ScanDevice scanDevice ;

    //扫描结果回调
    private IScanResult scanResult ;
    private final static String SCAN_ACTION = "scan.rcv.message";

    //提示性对话框
    public QMUITipDialog tipDialog;

    //QMUI风格
    public int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    //扫描接收广播
    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            //回调
            scanResult.onResult(new String(barocode));
            if(scanDevice != null)
                scanDevice.stopScan();
            }

    } ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_compare_id);

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
        //setBackBtnVisiable() ;
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

    //初始化扫描
    public void initScan(){
        scanDevice = new ScanDevice() ;
        if(scanDevice != null){
            scanDevice.openScan() ;
            scanDevice.setOutScanMode(0);//接收广播
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(scanReceiver, filter);

    }


    //关闭扫描
    public void closeScan(){
        if(scanDevice != null) {
            scanDevice.stopScan();
            scanDevice.closeScan();
            scanDevice = null ;
        }
        unregisterReceiver(scanReceiver);
    }

    //设置回调接口
    public void setScanResult(IScanResult scanResult){
        this.scanResult = scanResult ;
    }


    //显示提示性对话框
    public void showQMDialog(Context context, int iconType, int resString){
        if(tipDialog != null){
            tipDialog.dismiss();
        }
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(iconType)//设置提示图片类型
                .setTipWord(getResources().getString(resString))//提示内容
                .create() ;
        tipDialog.show();
    }

}
