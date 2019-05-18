package com.pl.ydapp.scan;

//扫描接口,返回扫描数据
public interface IScanResult {

    void onResult(String barcode) ;
}
