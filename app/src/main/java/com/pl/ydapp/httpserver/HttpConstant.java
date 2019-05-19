package com.pl.ydapp.httpserver;

public class HttpConstant {

    /**
     * ip地址
     */
    public final static String URL = "http://47.94.10.107:3388" ;

    public static String REQUEST_GET = "GET" ;

    public static String REQUEST_POST = "POST" ;

    public static String ACTION = "action" ;

    public static String DATA = "data" ;

    /**
     * 请求成功
     */
    public static int REQUEST_OK = 0 ;

    /**
     * 请求失败
     */
    public static int REQUEST_FAIL = 1 ;

    /**
     * 登陆
     */
    public final static String LOGIN = "/api/login" ;

    /**
     * 包装规格查询
     */
    public final static String QUERYPICK = "/api/queryPick" ;

    /**
     * 入仓请求
     */
    public final static String PARTIN = "/api/partIn" ;

    /**
     * 出仓请求
     */
    public final static String PORTOUT = "/api/partOut" ;

    /**
     * 零件查询
     */
    public final static String QUERY_PART_NUMBER = "/api/queryPartByNumber" ;

}
