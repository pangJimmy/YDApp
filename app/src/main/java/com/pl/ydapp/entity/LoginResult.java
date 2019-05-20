package com.pl.ydapp.entity;

//登陆请求的回应,有3层嵌套的json
public class LoginResult {
    /***
     *     response =  {"success":true,
     *     "data":{
     *     "user":{
     *     "id":6,
     *     "account":null,
     *     "realName":"杨  杰",
     *     "phone":"15555555555",
     *     "passwd":"555555",
     *     "provinceId":null,
     *     "cityId":410100,
     *     "areaId":null,
     *     "company":{"page":null,"limit":null,"id":2,
     *     "name":"郑州市金水区庙李镇丰庆路小学","zhiZhao":"",
     *     "provinceId":null,"cityId":"410100",
     *     "areaId":null,"location":null,
     *     "companyType":"XUEXIAO",
     *     "status":true,"createTime":null,
     *     "updateTime":null,"invoiceHeader":null,
     *     "invoiceTaxNo":"","contactPerson":null,
     *     "contactTel":null,"user":null
     *     },
     *     "modifyPwd":1
     *     },
     *     "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2In0.V-yz7adib9LQ3utF-2oWy014w0-uBBh33PqB3TQ_pVE"
     *     },
     *     "message":"登录成功",
     *     "code":0}
     */
    public  boolean success ;
    public Data data ;
    public String message ;
    public int code ;

    public static class Data{
        public User user ;
        public String token ;

        /**
         *      *     "user":{
         *     "id":6,
         *     "account":null,
         *     "realName":"杨  杰",
         *     "phone":"15555555555",
         *     "passwd":"555555",
         *     "provinceId":null,
         *     "cityId":410100,
         *     "areaId":null,
         *     "company":{"page":null,"limit":null,"id":2,
         *     "name":"郑州市金水区庙李镇丰庆路小学","zhiZhao":"",
         *     "provinceId":null,"cityId":"410100",
         *     "areaId":null,"location":null,
         *     "companyType":"XUEXIAO",
         *     "status":true,"createTime":null,
         *     "updateTime":null,"invoiceHeader":null,
         *     "invoiceTaxNo":"","contactPerson":null,
         *     "contactTel":null,"user":null
         *     },
         *     "modifyPwd":1
         *     },
         */
        public static class User{
            public int id ;
            public String account ;
            public String realName ;
            public String phone ;
            public String passwd ;
            public int provinceId ;
            public int cityId ;
            public int areaId ;
            public Company company ;
            public int modifyPwd ;

            /**
             *     "company":{
             *     "page":null,
             *     "limit":null,
             *     "id":2,
             *     "name":"郑州市金水区庙李镇丰庆路小学",
             *     "zhiZhao":"",
             *     "provinceId":null,
             *     "cityId":"410100",
             *     "areaId":null,
             *     "location":null,
             *     "companyType":"XUEXIAO",
             *     "status":true,
             *     "createTime":null,
             *     "updateTime":null,
             *     "invoiceHeader":null,
             *     "invoiceTaxNo":"",
             *     "contactPerson":null,
             *     "contactTel":null,
             *     "user":null
             *     },
             */
            public static class Company{
                public String page ;
                public String limit ;
                public int id ;
                public String name ;
                public String zhiZhao ;
                public String provinceId ;
                public String cityId ;
                public String areaId ;
                public String location ;
                public String companyType ;
                public boolean status ;
                public String createTime ;
                public String updateTime ;
                public String invoiceHeader ;
                public String invoiceTaxNo ;
                public String contactPerson ;
                public String contactTel ;
                public String user ;

            }
        }
    }
}
