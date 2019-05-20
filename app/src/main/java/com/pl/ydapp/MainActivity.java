package com.pl.ydapp;

import android.graphics.Bitmap;
import android.posapi.PosApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pl.ydapp.Util.BarcodeCreater;
import com.pl.ydapp.Util.BitmapTools;
import com.pl.ydapp.Util.PowerUtil;
import com.pl.ydapp.httpserver.HttpServer;
import com.pl.ydapp.print.PrintQueue;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String tag = "MainActivity" ;

    private Bitmap mBitmap = null;
    private PosApi mPosApi;
    private PrintQueue mPrintQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//        Button btn = findViewById(R.id.button) ;
//        btn.setOnClickListener(this);
//        Button btn2 = findViewById(R.id.button2) ;
//        btn2.setOnClickListener(this);
        //API接口测试
//        new Thread(new Runnable(){
//
//            @Override
//            public void run() {
//                HttpServer http = new HttpServer() ;
//                http.queryPartByNumber("123") ;
//                http.login("13512345678", "123");
//                http.queryPick("123") ;
//                http.partIn("12","12","12",1, "123","1234") ;
//                http.partOut("12","12","12",1, "123","1234") ;
//            }
//        }).start() ;
        //Power on
        PowerUtil.power("1");

        //测试打印机
        mPosApi = PosApi.getInstance(this);
        mPosApi.setOnComEventListener(mCommEventListener);
        mPosApi.initDeviceEx("/dev/ttyMT2");

    }

    PosApi.OnCommEventListener mCommEventListener = new PosApi.OnCommEventListener() {

        @Override
        public void onCommState(int cmdFlag, int state, byte[] resp, int respLen) {
            // TODO Auto-generated method stub
            switch (cmdFlag) {
                case PosApi.POS_INIT:
                    if (state == PosApi.COMM_STATUS_SUCCESS) {
                        Toast.makeText(getApplicationContext(), "Initialization success", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to initialize", Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出关闭打印机
        if (mPrintQueue != null) {
            mPrintQueue.close();
        }
        PowerUtil.power("0");
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.button:
//                mPosApi = PosApi.getInstance(this);
//                mPrintQueue = new PrintQueue(this, mPosApi);
//                mPrintQueue.init();
//                mPrintQueue.setOnPrintListener(new PrintQueue.OnPrintListener() {
//
//                    @Override
//                    public void onGetState(int state) {
//                        switch (state) {
//                            case 0:
//                                Toast.makeText(MainActivity.this, "Has paper!",
//                                        Toast.LENGTH_SHORT).show();
//
//                                break;
//                            case 1:
//
//                                Toast.makeText(MainActivity.this, "No paper!",
//                                        Toast.LENGTH_SHORT).show();
//
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onPrinterSetting(int state) {
//                        switch (state) {
//                            case 0:
//                                Toast.makeText(MainActivity.this, "Has paper",
//                                        Toast.LENGTH_SHORT).show();
//                                break;
//                            case 1:
//                                Toast.makeText(MainActivity.this, "No paper",
//                                        Toast.LENGTH_SHORT).show();
//                                break;
//                            case 2:
//                                Toast.makeText(MainActivity.this,
//                                        "Detected black mark", Toast.LENGTH_SHORT).show();
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        // TODO Auto-generated method stub
//                        Toast.makeText(MainActivity.this, "Print Finished!",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailed(int state) {
//                        // TODO Auto-generated method stub
//                        switch (state) {
//                            case PosApi.ERR_POS_PRINT_NO_PAPER:
////                                showTip(getString(R.string.print_no_paper));
//                                break;
//                            case PosApi.ERR_POS_PRINT_FAILED:
//                                break;
//                            case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
//                                break;
//                            case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
//                                break;
//                        }
//                    }
//                });
//                break ;
//            case R.id.button2:
//                try{
//
//
//                int mWidth = 150;
//                int mHeight = 150;
//                byte[] printData ;
//                //浓度60
//                int concentration = 60 ;
//                mBitmap = BarcodeCreater.encode2dAsBitmap("1234567890", mWidth,
//                        mHeight, 2);
//                printData = BitmapTools.bitmap2PrinterBytes(mBitmap);
//                mPrintQueue.addBmp(concentration, 100, mBitmap.getWidth(),
//                        mBitmap.getHeight(), printData);
//                StringBuffer buffer = new StringBuffer() ;
//                buffer.append("\n") ;
//                buffer.append("\n") ;
//                buffer.append("\n") ;
//                buffer.append("\n") ;
//                buffer.append("\n");
//                buffer.append("\n");
//                byte[] text = buffer.toString().getBytes("GBK");
//                //mPrintQueue.addText(concentration, mData);可用addText替换
//                addPrintTextWithSize(1, concentration, text);
//                mPrintQueue.printStart();
//                }catch (Exception e){
//
//                }
//                break ;
//        }



    }

    /*
     * Font size
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
