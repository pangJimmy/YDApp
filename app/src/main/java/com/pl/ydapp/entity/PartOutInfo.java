package com.pl.ydapp.entity;

public class PartOutInfo {
    /** {"success":true,
     * "data":{
     * "page":null,
     * "limit":null,
     * "id":13350,
     * "number":"10783549",
     * "name":"蓄电池总成",
     * "companyCode":null,
     * "company":"深圳理士奥电源技术有限公司",
     * "count":48,
     * "packId":13351,
     * "createTime":"2019-05-18T05:33:59.000+0000",
     * "username":null,
     * "status":"1",
     * "pack":{"page":null,
     * "limit":null,
     * "id":13351,
     * "name":"包装数-48件",
     * "count":5,
     * "amount":48,"
     * createTime":"2019-05-19T01:58:02.000+0000",
     * "status":"1"}},
     * "message":"查询成功","code":0}
     */

    public  boolean success ;
    public Data data ;
    public String message ;
    public int code ;


    public static class Data{
        public String page ;
        public String limit ;
        public int id ;
        public String number ;
        public String name ;
        public String companyCode ;
        public String company ;
        public int count ;
        public int packId ;
        public String createTime ;
        public String username ;
        public String status ;
        public Pack pack ;


        public static class Pack{
            public String page ;
            public String limit ;
            public int id ;
            public String name ;
            public int count ;
            public int amount ;
            public String createTime ;
            public String status ;

        }

    }

}
