package com.pl.ydapp.entity;

import java.util.List;

//包装规格
public class PackInfo {
    /***
     * {"success":true,
     * "data":[
     * {"page":null,
     * "limit":null,
     * "id":13348,
     * "name":"钢架-28件",
     * "count":0,
     * "amount":28,
     * "createTime":"2019-05-18T05:30:20.000+0000",
     * "status":"1"},
     * {"page":null,
     * "limit":null,
     * "id":13351,
     * "name":"包装数-48件",
     * "count":5,
     * "amount":48,
     * "createTime":"2019-05-19T01:58:02.000+0000",
     * "status":"1"},
     * {"page":null,
     * "limit":null,
     * "id":13353,
     * "name":"包装数-30件",
     * "count":6,
     * "amount":30,
     * "createTime":"2019-05-19T02:33:06.000+0000",
     * "status":null},
     * {"page":null,
     * "limit":null,
     * "id":13370,
     * "name":"包装数-40",
     * "count":0,
     * "amount":40,
     * "createTime":"2019-05-21T04:38:33.000+0000",
     * "status":"1"},
     * {"page":null,
     * "limit":null,
     * "id":13371,
     * "name":"包装数-400",
     * "count":0,
     * "amount":400,
     * "createTime":"2019-05-21T04:38:56.000+0000",
     * "status":"1"},
     * {"page":null,
     * "limit":null,
     * "id":13372,
     * "name":"包装数-5000",
     * "count":0,"amount":5000,
     * "createTime":"2019-05-21T04:39:16.000+0000",
     * "status":"1"},
     * {"page":null,
     * "limit":null,
     * "id":13373,
     * "name":"包装数-3000",
     * "count":0,
     * "amount":3000,"createTime":"2019-05-21T04:39:30.000+0000","status":"1"},{"page":null,"limit":null,"id":13374,"name":"包装数-26","count":0,"amount":26,"createTime":"2019-05-21T04:39:50.000+0000","status":"1"},{"page":null,"limit":null,"id":13375,"name":"包装数-144","count":0,"amount":144,"createTime":"2019-05-21T04:40:29.000+0000","status":"1"},{"page":null,"limit":null,"id":13376,"name":"包装数-15","count":0,"amount":15,"createTime":"2019-05-21T04:40:54.000+0000","status":"1"},{"page":null,"limit":null,"id":13377,"name":"包装数-6","count":0,"amount":6,"createTime":"2019-05-21T04:41:14.000+0000","status":"1"},{"page":null,"limit":null,"id":13378,"name":"包装数-10","count":0,"amount":10,"createTime":"2019-05-21T04:41:35.000+0000","status":"1"},{"page":null,"limit":null,"id":13379,"name":"包装数-200","count":0,"amount":200,"createTime":"2019-05-21T04:41:55.000+0000","status":"1"},{"page":null,"limit":null,"id":13380,"name":"包装数-600","count":0,"amount":600,"createTime":"2019-05-21T04:42:12.000+0000","status":"1"},{"page":null,"limit":null,"id":13388,"name":"未知数量包装","count":0,"amount":0,"createTime":"2019-05-21T04:51:35.000+0000","status":"1"},{"page":null,"limit":null,"id":13394,"name":"包装数-20","count":0,"amount":20,"createTime":"2019-05-21T04:54:41.000+0000","status":"1"}],
     * "message":"查询成功",
     * "code":0}
     */

    public boolean success ;
    public List<Data>  data ;
    public String message ;
    public int code ;

    public static class Data{
        /**
         *{"page":null,
         * "limit":null,
         * "id":13371,
         * "name":"包装数-400",
         * "count":0,
         * "amount":400,
         * "createTime":"2019-05-21T04:38:56.000+0000",
         * "status":"1"},
         */
        public String page ;
        public String limit ;
        public int id ;
        public String name ;
        public int count ;
        public int amount ;
        public String createTime ;
        public String status ;

//        public static class Page{
//            /**
//             *{"page":null,
//             * "limit":null,
//             * "id":13371,
//             * "name":"包装数-400",
//             * "count":0,
//             * "amount":400,
//             * "createTime":"2019-05-21T04:38:56.000+0000",
//             * "status":"1"},
//             */
//            public String page ;
//            public String limit ;
//            public int id ;
//            public String name ;
//            public int count ;
//            public int amount ;
//            public String createTime ;
//            public String status ;
//        }
    }
}
