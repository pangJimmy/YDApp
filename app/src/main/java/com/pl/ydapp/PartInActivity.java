package com.pl.ydapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pl.ydapp.Util.Logger;
import com.pl.ydapp.Util.VoiceTip;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.scan.IScanResult;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

//入仓
public class PartInActivity extends BaseActivity implements View.OnClickListener{

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
    //厂商
    private String vendor ;
    //零件名称
    private String partName ;
    //经办者
    private String operator ;
    //规格名称
    private String psName ;

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
//        editPS = findViewById(R.id.editText_ps) ;
        btnOK = findViewById(R.id.button_ok) ;
        btnCancel = findViewById(R.id.button_cancel) ;
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_ok:
                String test = "零件号：123131\n零件名称：电池\n包装规格:箱-30\n出库数量：30\n" ;
                showComfirmDialog(test) ;
                break ;
            case R.id.button_cancel:

                break ;
        }
    }

    //弹出确认窗口，再次确认下入仓数据是否正确
    private void showComfirmDialog(String msg){
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle(R.string.comfirm_part_in_info)
                .setMessage(msg)
                .addAction(getResources().getString(R.string.cancel), null)
                .addAction(getResources().getString(R.string.ok), new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //确认提交
                    }
                })
                .create()
                .show();
    }
}
