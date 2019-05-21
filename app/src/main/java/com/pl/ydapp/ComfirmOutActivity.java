package com.pl.ydapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.pl.ydapp.R;
import com.pl.ydapp.application.MApplication;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.entity.PartOutInfo;
import com.pl.ydapp.entity.Response;
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
    private Context context ;
    //零件ID
    private String partId ;
    //出仓数量
    private int count ;
    //厂商
    private String vendor ;
    //零件名称
    private String partName ;
    //经办者
    private String operator ;
    //规格名称
    private String psName ;
    //规格id
    private int packID ;
    //用于异步执行线程
    private Handler handler = new Handler() ;

    private MApplication mapp ;

    private boolean outFlag = false ;
    //出仓零件信息
    private PartOutInfo partInfo ;
    //查询零件信息
    private Runnable getPartInfoTask = new Runnable() {
        @Override
        public void run() {
            HttpServer httpServer = new HttpServer(context) ;
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
                        //出现异常时对话框关不了，强制关闭
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
        context = this ;
        mapp = (MApplication) getApplication();
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
        //信息用返回 的realName
        String realName = mapp.getLoginResult().data.user.realName ;
        if(realName != null){
            editOperator.setText(realName);
        }
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
                        operator = partInfo.data.username ;
                        partName = partInfo.data.name ;
                        psName = partInfo.data.pack.name ;
                        vendor = partInfo.data.company ;
                        count = partInfo.data.pack.amount ;
                        packID = partInfo.data.packId ;
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
                        showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.no_this_id);
                    }


                }else {

                    //网络请求数据失败
                    showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.request_http_fail);
                }
            }
        });

    }

    //出仓线程
    private Runnable partOutTask = new Runnable() {
        @Override
        public void run() {
            operator = editOperator.getText().toString() ;
            HttpServer http = new HttpServer(context) ;
            Response response = http.partOut(partId,
                    partName,
                    Integer.valueOf(packID).toString(),
                    count,
                    vendor,
                    operator) ;
            //处理网络返回数据
            handleResponse(response);
            //取消提示
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(tipDialog != null){
                        tipDialog.dismiss();
                        //出仓成功跳转
                        if(outFlag){
                            finish();
                            Intent intent = new Intent(ComfirmOutActivity.this, CompareIDActivity.class) ;
                            startActivity(intent);
                        }

                    }
                }
            }, 2000) ;
        }
    } ;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_ok://确认
                vendor = editVendor.getText().toString() ;
                //提交出仓数据
                new Thread(partOutTask).start();
                //正在加载
                showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_LOADING,  R.string.loading);
                break ;
            case R.id.button_cancel://取消
                finish();
                break ;

        }
    }

    //处理出仓返回数据
    private void handleResponse(final  Response response){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(response != null){
                    //数据返回正确，success为true code = 0
                    if(response.success && response.code == HttpConstant.REQUEST_OK){
                        tipDialog.dismiss();
                        //出仓成功，返回原界面
                        showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_SUCCESS,  R.string.part_out_success);
                        outFlag = true ;

                    }else{
                        //出仓失败
                        showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.part_out_fail);
                    }
                }else{
                    //网络请求数据失败
                    showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.request_http_fail);
                }
            }
        }) ;
    }
}
