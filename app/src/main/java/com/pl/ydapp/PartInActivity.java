package com.pl.ydapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.ydapp.Util.Logger;
import com.pl.ydapp.Util.VoiceTip;
import com.pl.ydapp.application.MApplication;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.entity.PackInfo;
import com.pl.ydapp.entity.Response;
import com.pl.ydapp.httpserver.HttpConstant;
import com.pl.ydapp.httpserver.HttpServer;
import com.pl.ydapp.scan.IScanResult;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.List;

//入仓
public class PartInActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private EditText editPart ;//零件号
    private EditText editPartName ;//零件名称
    private EditText editOutNumber ;//出库数量
    private EditText editVendor ;//厂商
    private EditText editOperator ;//经办人
    private Spinner spinnerPs ;//包装规格
    private Button btnOK ;
    private Button btnCancel ;

    //零件ID
    private String partId ;
    //出仓数量
    private int count ;
    //规格 ID
    private int psID = 0;
    //厂商
    private String vendor ;
    //零件名称
    private String partName ;
    //经办者
    private String operator ;
    //规格名称
    private String psName ;

    private Context context ;

    //所有规格
    private List<PackInfo.Data>  listPack ;
    private  PackInfo packInfo ;
    private MApplication mapp ;
    //提交入仓数据后的返回
    private Response response ;
    //获取扫描结果
    private IScanResult scanResult = new IScanResult() {
        @Override
        public void onResult(String barcode) {
            VoiceTip.play(1, 0);
            if(barcode != null && barcode.length() < 10){
                //二维码中只有零件号
                editPart.setText(barcode);
            }else{
                //二维码的数据为xxx_零件号_xxx_xxx，在第二段中包含
                String[] mBarcode = barcode.split("_") ;
                if(mBarcode != null && mBarcode.length > 1){
                    //扫描结果
                    editPart.setText(mBarcode[1]);
                }
            }


        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_part_in);
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.part_in);
        setBackBtnVisiable();

        context = this ;
        mapp = (MApplication) getApplication();
        initView();
        //设置扫描结果回调
        setScanResult(scanResult);
        //提示音
        VoiceTip.initSoundPool(this);
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


    private void initView() {
        editPart = findViewById(R.id.editText_parts_id) ;
        editPartName = findViewById(R.id.editText_parts_name) ;
        editOutNumber = findViewById(R.id.editText_out_number) ;
        editVendor = findViewById(R.id.editText_vendor) ;
        editOperator = findViewById(R.id.editText_operator) ;
        spinnerPs = findViewById(R.id.spinner_ps) ;
        btnOK = findViewById(R.id.button_ok) ;
        btnCancel = findViewById(R.id.button_cancel) ;
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        //经办者
        editOperator.setText(mapp.getLoginResult().data.user.realName);
        spinnerPs.setOnItemSelectedListener(this);
        showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.loading);
        new Thread(getPackTask).start();

    }

    //查询规格
    private Runnable getPackTask = new Runnable() {
        @Override
        public void run() {
            //查询所有规格
            HttpServer http = new HttpServer(context) ;
            packInfo = http.queryPick(null) ;
            handleListPack();
        }
    } ;

    //查询规格
    private Runnable partInTask = new Runnable() {
        @Override
        public void run() {
            //查询所有规格
            HttpServer http = new HttpServer(context) ;
            response = http.partIn(partId,partName, "" + psID,count , vendor, operator);
            handlePartIn();
        }
    } ;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_ok:
                comfirmPartIn() ;
                break ;
            case R.id.button_cancel:
                new Thread(getPackTask).start();
                break ;
        }
    }

    private QMUIDialog qmuiDialog ;

    //弹出确认窗口，再次确认下入仓数据是否正确
    private void showComfirmDialog(String msg){
        qmuiDialog = new QMUIDialog.MessageDialogBuilder(this)
                .setTitle(R.string.comfirm_part_in_info)
                .setMessage(msg)
                .addAction(getResources().getString(R.string.cancel), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        qmuiDialog.dismiss();
                    }
                })
                .addAction(getResources().getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.loading);
                        //确认提交
                        new Thread(partInTask).start();
                        qmuiDialog.dismiss();
                    }
                })
                .create();
        qmuiDialog.show();
    }

    //处理规格
    private void handleListPack(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(packInfo == null){
                    showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, R.string.unknow_network_err);
                    dismiss();
                    return ;
                }
                if(packInfo.success && packInfo.code == HttpConstant.REQUEST_OK){
                    listPack = packInfo.data ;
                    //填充规格
                    spinnerPs.setAdapter(new Madapter());
                }else{
                    //给出相应的提示
                    if(packInfo.message != null && packInfo.message.length() > 0){
                        showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, packInfo.message);
                    }else{
                        showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL, R.string.unknow_network_err);
                    }
                }
                dismiss() ;
            }
        });
    }


    //处理入仓返回数据
    private void handlePartIn(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(response != null){
                    //数据返回正确，success为true code = 0
                    if(response.success && response.code == HttpConstant.REQUEST_OK){
                        tipDialog.dismiss();
                        //入仓成功，返回原界面
                        Toast.makeText(context, R.string.part_in_success, Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(tipDialog != null) {
                                    tipDialog.dismiss();
                                }
                                finish();
                            }
                        }, 1500) ;

                    }else{
                        //入仓失败
                        showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.part_in_fail);
                        dismiss() ;
                    }
                }else{
                    //网络请求数据失败
                    showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.request_http_fail);
                    dismiss() ;
                }

            }
        }) ;
    }



    //提交入仓数据
    private void comfirmPartIn(){
        partId  = editPart.getText().toString().trim() ;
        partName = editPartName.getText().toString().trim() ;
        String countStr = editOutNumber.getText().toString().trim();
        vendor = editVendor.getText().toString().trim() ;
        operator = editOperator.getText().toString().trim() ;
        if(partId == null || partId.length() == 0){
            Toast.makeText(context,"零件号为空，请扫码或者输入", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(countStr == null || countStr.length() == 0){
            Toast.makeText(context,"入仓数量为空，请输入", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(partName == null || partName.length() == 0){
            Toast.makeText(context,"零件名称为空，请输入", Toast.LENGTH_SHORT).show();
            return ;
        }
        if(vendor == null || vendor.length() == 0){
            Toast.makeText(context,"供应商为空，请输入", Toast.LENGTH_SHORT).show();
            return ;
        }
        String msg = context.getResources().getString(R.string.part_in_info) ;
        msg = String.format(msg, partId,partName, psName, countStr, vendor, operator) ;
        //弹出确认提交对话框
        showComfirmDialog(msg) ;
    }

    //关闭提示窗口
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(listPack != null && !listPack.isEmpty()){
            //规格 ID
            psID = listPack.get(position).id ;
            psName = listPack.get(position).name ;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //规格适配器
    private class Madapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listPack.size();
        }

        @Override
        public Object getItem(int position) {
            return listPack.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null ;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner, null) ;
                holder = new ViewHolder();
                holder.tv = convertView.findViewById(R.id.textView_pack) ;
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(listPack != null && !listPack.isEmpty()){
                holder.tv.setText(listPack.get(position).name);
            }

            return convertView;
        }

        class ViewHolder{
            TextView tv ;
        }
    }
}
