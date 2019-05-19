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
import com.pl.ydapp.Util.PowerUtil;
import com.pl.ydapp.base.BaseActivity;
import com.pl.ydapp.print.PrintQueue;
import com.pl.ydapp.print.PrintQueue.OnPrintListener;

//打印二维码
public class PrintActivity extends BaseActivity implements View.OnClickListener{


    private EditText editPartID ;
    private EditText editCount ;
    private Button btnPrint ;

    private String partID ;
    private int count ;
    private String vendor ;
    private String partName ;
    private Context context ;

    //////////打印机/////////
    private boolean initFlag = false ;
    private Bitmap mBitmap = null;
    private PosApi mPosApi;
    private PrintQueue mPrintQueue = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_print);
        super.onCreate(savedInstanceState);

        setToolbarTitle(R.string.print_barcode);
        setBackBtnVisiable();
        partID = getIntent().getStringExtra("partid") ;
        vendor = getIntent().getStringExtra("vendor") ;
        partName = getIntent().getStringExtra("partname") ;
        count = getIntent().getIntExtra("count", 0) ;
        context = this ;
        initView();

        //设置打印机
        PowerUtil.power("1");
        mPosApi = PosApi.getInstance(this);
        mPosApi.setOnComEventListener(mCommEventListener);
        mPosApi.initDeviceEx("/dev/ttyMT2");
    }

    private void initView() {
        editPartID = findViewById(R.id.editText_parts_id) ;
        editCount = findViewById(R.id.editText_count) ;
        btnPrint = findViewById(R.id.button_print) ;
        if(partID != null){
            editPartID.setText(partID);
        }
        if(count > 0){
            editCount.setText(Integer.valueOf(count).toString());
        }
        btnPrint.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出关闭打印机
        if (mPrintQueue != null) {
            mPrintQueue.close();
        }
        PowerUtil.power("0");
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
            switch (state) {
                case PosApi.ERR_POS_PRINT_NO_PAPER:
//                                showTip(getString(R.string.print_no_paper));
                    break;
                case PosApi.ERR_POS_PRINT_FAILED:
                    break;
                case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                    break;
                case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                    break;
            }
        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onGetState(int state) {

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
            case R.id.button_print:
                print2D(partID, vendor,partName, 2) ;
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
            while(count == 0){
                //留出空白
                buffer = new StringBuffer() ;
                buffer.append("\n") ;
                buffer.append("\n") ;
                buffer.append("\n") ;
                buffer.append("\n");
                buffer.append("\n");
                text = buffer.toString().getBytes("GBK");
                //mPrintQueue.addText(concentration, mData);可用addText替换
                addPrintTextWithSize(1, concentration, text);
                //数据添加完后打印
                mPrintQueue.printStart();
                return ;
            }
            buffer = new StringBuffer() ;
            buffer.append("零件号：" + partID) ;
            buffer.append("\n");
            text = buffer.toString().getBytes("GBK");
            addPrintTextWithSize(2, concentration, text);
            buffer = new StringBuffer() ;
            buffer.append("名称：" + partName) ;
            buffer.append("\n");
            buffer.append("厂商：" + vendor) ;
            buffer.append("\n");
            text = buffer.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);
            int mWidth = 150;
            int mHeight = 150;

            mBitmap = BarcodeCreater.encode2dAsBitmap("1234567890", mWidth,
                    mHeight, 2);
            printData = BitmapTools.bitmap2PrinterBytes(mBitmap);
            mPrintQueue.addBmp(concentration, 0, mBitmap.getWidth(),
                    mBitmap.getHeight(), printData);


            count-- ;
            //递归调用
            print2D(partID,vendor,partName,count) ;
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
