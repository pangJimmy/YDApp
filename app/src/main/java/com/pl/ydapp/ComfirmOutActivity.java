package com.pl.ydapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pl.ydapp.R;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.entity.PartOutInfo;
import com.pl.ydapp.httpserver.HttpConstant;
import com.pl.ydapp.httpserver.HttpServer;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class ComfirmOutActivity extends BaseActivity implements View.OnClickListener{

    private EditText editPart ;//零件号
    private EditText editPartName ;//零件名称
    private EditText editOutNumber ;//出库数量
    private EditText editVendor ;//厂商
    private EditText editOperator ;//经办人
    private EditText editPS ;//包装规格
    private Button btnOK ;
    private Button btnCancel ;
    //零件ID
    private String partId ;
    //出仓数量
    private int count ;
    private String vendor ;
    private String partName ;

    private Handler handler = new Handler() ;

    private QMUITipDialog tipDialog;
    //出仓零件信息
    private PartOutInfo partInfo ;
    //查询零件信息
    private Runnable getPartInfoTask = new Runnable() {
        @Override
        public void run() {
            HttpServer httpServer = new HttpServer() ;
            if(partId != null){
                partInfo = httpServer.queryPartByNumber(partId) ;
                //对网络查询数据进行处理
                hadleData(partInfo) ;
            }
            //取消提示
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(tipDialog != null){
                        tipDialog.dismiss();
                        finish();
                    }
                }
            }, 2000) ;
        }
    } ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_confirm_out);
        super.onCreate(savedInstanceState);
        partId = getIntent().getStringExtra("partid") ;
        setTitle(R.string.part_out);
        setBackBtnVisiable();
        initView() ;

    }

    private void initView() {
        editPart = findViewById(R.id.editText_parts_id) ;
        editPartName = findViewById(R.id.editText_parts_name) ;
        editOutNumber = findViewById(R.id.editText_out_number) ;
        editVendor = findViewById(R.id.editText_vendor) ;
        editOperator = findViewById(R.id.editText_operator) ;
        editPS = findViewById(R.id.editText_ps) ;
        btnOK = findViewById(R.id.button_ok) ;
        btnCancel = findViewById(R.id.button_cancel) ;
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //启动线程查询
        new Thread(getPartInfoTask).start();
        //正在加载
        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getResources().getString(R.string.loading))
                .create();
        tipDialog.show();
    }

    //处理网络请求数据
    private void hadleData(final PartOutInfo partInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(partInfo != null){
                    //数据返回OK
                    if(partInfo.success && partInfo.code == HttpConstant.REQUEST_OK){
                        String partId = partInfo.data.number ;
                        String operator = partInfo.data.username ;
                        partName = partInfo.data.name ;
                        String psName = partInfo.data.pack.name ;
                        vendor = partInfo.data.company ;
                        count = partInfo.data.pack.amount ;
                        if(partId != null && !"null".equals(partId)){
                            editPart.setText(partId);
                        }
                        if(operator != null && !"null".equals(operator)){
                            editOperator.setText(operator);
                        }
                        if(partName != null && !"null".equals(partName)){
                            editPartName.setText(partName);
                        }
                        if(psName != null && !"null".equals(psName)){
                            editPS.setText(psName);
                        }
                        editOutNumber.setText(""+count);
                        if(vendor != null && !"null".equals(vendor)){
                            editVendor.setText(vendor);
                        }
                        //取消对话框加载
                        if(tipDialog != null){
                            tipDialog.dismiss();
                            tipDialog = null ;
                        }
                    }else{
                        //未查询到该零件信息
                        tipDialog.dismiss();

                        tipDialog = new QMUITipDialog.Builder(ComfirmOutActivity.this)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                .setTipWord(getResources().getString(R.string.no_this_id))
                                .create() ;
                        tipDialog.show();
                    }


                }else {
                    tipDialog.dismiss();
                    //网络请求数据失败
                    tipDialog = new QMUITipDialog.Builder(ComfirmOutActivity.this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                            .setTipWord(getResources().getString(R.string.request_http_fail))
                            .create() ;
                    tipDialog.show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_ok://确认
                //跳转到打印二维码
                Intent intent = new Intent(this, PrintActivity.class) ;
                intent.putExtra("partid", partId) ;
                intent.putExtra("vendor", vendor) ;
                intent.putExtra("partname", partName) ;
                intent.putExtra("count", count) ;
                startActivity(intent);
                break ;
            case R.id.button_cancel://取消
                finish();
                break ;

        }
    }
}
