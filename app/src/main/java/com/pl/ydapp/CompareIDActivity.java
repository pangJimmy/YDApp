package com.pl.ydapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.pl.ydapp.Util.Logger;
import com.pl.ydapp.Util.VoiceTip;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.httpserver.HttpServer;
import com.pl.ydapp.scan.IScanResult;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

//对比零件ID
public class CompareIDActivity extends BaseActivity {

    private String tag = "CompareIDActivity" ;
    //零件号1
    private EditText editPart1 ;
    //零件号2
    private EditText editPart2 ;

    //获取扫描结果
    private IScanResult scanResult = new IScanResult() {
        @Override
        public void onResult(String barcode) {
            Logger.e(tag,"barcode = " +  barcode);
            //二维码的数据为xxx_零件号_xxx_xxx
            String[] mBarcode = barcode.split("_") ;
            if(mBarcode != null && mBarcode.length > 1){
                //比较扫描结果
                comparePart1Part2(mBarcode[1]) ;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_compare_id);
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.part_out);
        //设置返回键
        setBackBtnVisiable();
        initView() ;
        //设置扫描结果回调
        setScanResult(scanResult);
        //提示音
        VoiceTip.initSoundPool(this);
    }

    private void initView() {
        editPart1 = findViewById(R.id.editText_parts_id1) ;
        editPart2 = findViewById(R.id.editText_parts_id2) ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeScan();
    }

    //比较 Part1和Part2
    private void comparePart1Part2(final String barcode){
        String part1 = editPart1.getText().toString();
        //第一次先扫part1
        if(part1 == null || "".equals(part1)) {
            editPart1.setText(barcode);
            VoiceTip.play(1, 0);
        }else{
            editPart2.setText(barcode);
            //零件1和零件不匹配，弹出提示和播放错误提示音
            if(!part1.equals(barcode)){
                showErrorDialog() ;
                VoiceTip.play(2, 0);
            }else{
                VoiceTip.play(1, 0);
                //零件号一致时，查询后台
                showSuccessDialog(this, barcode);
                /**
                 * 测试数据
                 * 10783549
                 10302681
                 10418246

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpServer http = new HttpServer() ;
                        http.queryPartByNumber("10783549") ;
                    }
                }).start();
                 */
            }
            //VoiceTip.play(1, 1);
        }
    }


    //弹出错误提示
    private void showErrorDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setIcon(R.drawable.ic_compare_error);
        builder.setTitle(R.string.part_compare_error) ;
        builder.setNegativeButton(R.string.ok,null) ;
        builder.create().show();
    }

    private void showSuccessDialog(final Context context , final String barcode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setIcon(R.drawable.ic_compare_ok);
        builder.setTitle(R.string.part_compare_ok) ;
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, ComfirmOutActivity.class) ;
                intent.putExtra("partid", barcode) ;
                startActivity(intent);
            }
        }) ;
        builder.create().show();
    }
}
