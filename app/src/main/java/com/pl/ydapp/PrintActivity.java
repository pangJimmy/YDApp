package com.pl.ydapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.posapi.PosApi;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pl.ydapp.Util.BarcodeCreater;
import com.pl.ydapp.Util.BitmapTools;
import com.pl.ydapp.Util.Logger;
import com.pl.ydapp.Util.PowerUtil;
import com.pl.ydapp.Util.VoiceTip;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.entity.PartOutInfo;
import com.pl.ydapp.httpserver.HttpConstant;
import com.pl.ydapp.httpserver.HttpServer;
import com.pl.ydapp.print.PrintQueue;
import com.pl.ydapp.print.PrintQueue.OnPrintListener;
import com.pl.ydapp.scan.IScanResult;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

//打印二维码
public class PrintActivity extends BaseActivity implements View.OnClickListener{


    private EditText editPartID ;
    private EditText editCount ;
    private Button btnPrint ;

    private String partID ;
    //默认打印次数5次
    private int count = 5 ;
    private String vendor ;
    private String partName ;
    private Context context ;

    //////////打印机/////////
    private boolean initFlag = false ;
    private Bitmap mBitmap = null;
    private PosApi mPosApi;
    private PrintQueue mPrintQueue = null;
    //零件信息
    private PartOutInfo partInfo ;

    //是否查询
    boolean isQuery = false ;
    //获取扫描结果
    private IScanResult scanResult = new IScanResult() {
        @Override
        public void onResult(String barcode) {
            VoiceTip.play(1, 0);
            if(barcode != null && barcode.length() < 10){
                //二维码中只有零件号
                editPartID.setText(barcode);
                partID = barcode ;
            }else{
                //二维码的数据为xxx_零件号_xxx_xxx，在第二段中包含
                String[] mBarcode = barcode.split("_") ;
                if(mBarcode != null && mBarcode.length > 1){
                    //扫描结果
                    editPartID.setText(mBarcode[1]);
                    partID = mBarcode[1] ;
                }
            }
            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.loading);
            isQuery = true ;
            //查询
            new Thread(getPartInfoTask).start();

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_print);
        super.onCreate(savedInstanceState);

        setToolbarTitle(R.string.print_barcode);
        setBackBtnVisiable();
//        partID = getIntent().getStringExtra("partid") ;
//        vendor = getIntent().getStringExtra("vendor") ;
//        partName = getIntent().getStringExtra("partname") ;
//        count = getIntent().getIntExtra("count", 0) ;
        context = this ;
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

        //设置打印机
        PowerUtil.power("1");
        mPosApi = PosApi.getInstance(this);
        mPosApi.setOnComEventListener(mCommEventListener);
        mPosApi.initDeviceEx("/dev/ttyMT2");
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeScan();
        if (mPosApi != null) {
            mPosApi.closeDev();
        }
        //退出关闭打印机
        if (mPrintQueue != null) {
            mPrintQueue.close();
        }
        PowerUtil.power("0");
    }


    //查询零件信息
    private Runnable getPartInfoTask = new Runnable() {
        @Override
        public void run() {
            HttpServer httpServer = new HttpServer(context) ;
            if(partID != null){
                partInfo = httpServer.queryPartByNumber(partID) ;
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
                    }
                }
            }, 1000) ;
        }
    } ;

    private void initView() {
        editPartID = findViewById(R.id.editText_parts_id) ;
        editCount = findViewById(R.id.editText_count) ;
        btnPrint = findViewById(R.id.button_print) ;
//        if(partID != null){
//            editPartID.setText(partID);
//        }
//        if(count > 0){
//            editCount.setText(Integer.valueOf(count).toString());
//        }

        editCount.setText(Integer.valueOf(count).toString());
        btnPrint.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //处理网络请求数据
    private void hadleData(final PartOutInfo partInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(partInfo != null){
                    //数据返回OK
                    if(partInfo.success && partInfo.code == HttpConstant.REQUEST_OK && partInfo != null && partInfo.data != null){
                        //partID = partInfo.data.number ;
                        partName = partInfo.data.name ;
                        vendor = partInfo.data.company ;
                        //count = partInfo.data.pack.amount ;
                        //取消对话框加载
                        if(tipDialog != null){
                            tipDialog.dismiss();
                            tipDialog = null ;
                        }
                        //如果是手动输入，直接开始打印
                        if(!isQuery){
                            //
                            Logger.e("hadleData" , "print partName = " + partName);
                            Logger.e("hadleData" , "print vendor = " + vendor);
                            Logger.e("hadleData" , "print isQuery = " + isQuery);
                            showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.printing);
                            //使用线程打印
                            print2D(partID,vendor,partName,count);

                        }
                    }else{
                        //未查询到该零件信息
                        showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.no_this_id);
                    }


                }else {
                    //网络请求数据失败
                    showQMDialog(context,QMUITipDialog.Builder.ICON_TYPE_FAIL,  R.string.request_http_fail);
                }
                isQuery = false ;

            }
        });

    }


    //打印机初始事件监听
    PosApi.OnCommEventListener mCommEventListener = new PosApi.OnCommEventListener() {

        @Override
        public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
            // TODO Auto-generated method stub
            switch (cmdFlag) {
                case PosApi.POS_INIT:
                    if (state == PosApi.COMM_STATUS_SUCCESS) {
//                        Toast.makeText(getApplicationContext(), "Initialization success", Toast.LENGTH_SHORT)
//                                .show();
                        initFlag = true ;
                        mPosApi = PosApi.getInstance(context);
                        mPrintQueue = new PrintQueue(context, mPosApi);
                        mPrintQueue.init();
                        mPrintQueue.setOnPrintListener(mPrinterListener);
                    } else {
                        initFlag = false ;
                        Toast.makeText(getApplicationContext(), "Failed to initialize", Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        }
    };

    //打印状态监听
    private PrintQueue.OnPrintListener mPrinterListener = new PrintQueue.OnPrintListener(){

        @Override
        public void onFailed(int state) {
            if(tipDialog != null){
                tipDialog.dismiss();
            }
            switch (state) {
                case PosApi.ERR_POS_PRINT_NO_PAPER:
//                                showTip(getString(R.string.print_no_paper));
                    //无纸张
                    Toast.makeText(context, R.string.no_paper, Toast.LENGTH_SHORT).show();
                    break;
                case PosApi.ERR_POS_PRINT_FAILED:
                case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                    //打印失败
                    Toast.makeText(context, R.string.print_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onFinish() {
            if(tipDialog != null){
                tipDialog.dismiss();
            }
            Toast.makeText(context, R.string.print_finish, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGetState(int state) {
            Logger.e("onGetState" , "onGetState = " + state);
        }

        @Override
        public void onPrinterSetting(int state) {
            switch (state) {
                case 0:
                    Toast.makeText(context, "Has paper",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(context, "No paper",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context,
                            "Detected black mark", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    } ;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_print://打印
                partID = editPartID.getText().toString() ;
                String countStr = editCount.getText().toString() ;
                if(countStr != null && countStr.length() > 0){
                    count = Integer.valueOf(countStr) ;
                }else{
                    Toast.makeText(this, R.string.please_put_count, Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(partID != null && partID.length() > 0){
                }else{
                    Toast.makeText(this, R.string.please_put_part_id, Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(!isQuery){
                    //手动输入，则要查询后才打印
                    showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.loading);
                    //查询
                    new Thread(getPartInfoTask).start();
                }else{
                    showQMDialog(context, QMUITipDialog.Builder.ICON_TYPE_LOADING, R.string.printing);
                    //打印
                    print2D(partID, vendor,partName, count) ;
                }

                break ;
        }
    }

    //打印二维码
    private void print2D(String partID, String vendor, String partName, int count){
        try{
            byte[] printData ;
            StringBuffer buffer ;
            //浓度60
            int concentration = 60 ;
            byte[] text ;
            while(count > 0){
                buffer = new StringBuffer() ;
                //每个标签打印间距加大
                buffer.append("\n") ;
                buffer.append("\n") ;
                buffer.append("零件号：" + partID) ;
                buffer.append("\n");
                text = buffer.toString().getBytes("GBK");
                Logger.e("print", buffer.toString());
                addPrintTextWithSize(2, concentration, text);
                buffer = new StringBuffer() ;
                buffer.append("零件名称：" + partName) ;
                buffer.append("\n");
                buffer.append("供应商：" + vendor) ;
                buffer.append("\n");
                text = buffer.toString().getBytes("GBK");
                Logger.e("print", buffer.toString());
                addPrintTextWithSize(1, concentration, text);
                int mWidth = 250;
                int mHeight = 250;
                //打印二维码图片
                mBitmap = BarcodeCreater.encode2dAsBitmap(partID, mWidth,
                        mHeight, 2);
                printData = BitmapTools.bitmap2PrinterBytes(mBitmap);
                mPrintQueue.addBmp(concentration, 60, mBitmap.getWidth(),
                        mBitmap.getHeight(), printData);
                count-- ;

            }


            //留出空白
            buffer = new StringBuffer() ;
            buffer.append("\n") ;
            buffer.append("\n") ;
            buffer.append("\n") ;
            buffer.append("\n") ;
            text = buffer.toString().getBytes("GBK");
            //mPrintQueue.addText(concentration, mData);可用addText替换
            addPrintTextWithSize(1, concentration, text);
            //数据添加完后打印
            mPrintQueue.printStart();


            //递归调用
            //print2D(partID,vendor,partName,count) ;

        }catch (Exception e){

        }
    }

    /*
     *设置字体
     */
    private void addPrintTextWithSize(int size, int concentration, byte[] data) {
        if (data == null) {
            return;
        }
        // 2 size Font
        byte[] _2x = new byte[] { 0x1b, 0x57, 0x02 };
        // 1 size Font
        byte[] _1x = new byte[] { 0x1b, 0x57, 0x01 };
        byte[] mData = null;
        if (size == 1) {
            mData = new byte[3 + data.length];
            System.arraycopy(_1x, 0, mData, 0, _1x.length);
            System.arraycopy(data, 0, mData, _1x.length, data.length);
            mPrintQueue.addText(concentration, mData);
        } else if (size == 2) {
            mData = new byte[3 + data.length];
            System.arraycopy(_2x, 0, mData, 0, _2x.length);
            System.arraycopy(data, 0, mData, _2x.length, data.length);
            mPrintQueue.addText(concentration, mData);
        }
    }
}
