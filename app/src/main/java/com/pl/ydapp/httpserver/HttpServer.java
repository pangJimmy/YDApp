package com.pl.ydapp.httpserver;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.ydapp.PartInActivity;
import com.pl.ydapp.Util.Logger;
import com.pl.ydapp.Util.MyShared;
import com.pl.ydapp.entity.LoginResult;
import com.pl.ydapp.entity.PartOutInfo;
import com.pl.ydapp.entity.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServer {

    private String tag = "HttpServer" ;

    private Context context ;
    private String URL = null ;//ip

    public HttpServer(Context context){
        this.context = context ;
        URL = new MyShared().getIP(context) ;
    }


    /***
     *     response =  {"success":true,
     *     "data":{"user":{"id":6,"account":null,
     *     "realName":"杨  杰",
     *     "phone":"15555555555",
     *     "passwd":"555555","provinceId":null,
     *     "cityId":410100,"areaId":null,
     *     "company":{"page":null,"limit":null,"id":2,
     *     "name":"郑州市金水区庙李镇丰庆路小学","zhiZhao":"",
     *     "provinceId":null,"cityId":"410100",
     *     "areaId":null,"location":null,
     *     "companyType":"XUEXIAO",
     *     "status":true,"createTime":null,
     *     "updateTime":null,"invoiceHeader":null,
     *     "invoiceTaxNo":"","contactPerson":null,
     *     "contactTel":null,"user":null},"modifyPwd":1},
     *     "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2In0.V-yz7adib9LQ3utF-2oWy014w0-uBBh33PqB3TQ_pVE"},"
     *     message":"登录成功",
     *     "code":0}
     */
    /****
     * 登陆接口
     * 测试账号
     * 帐号 155 5555 5555
     * 密码 555 555
     * @return
     */
    public LoginResult login(String phone, String passwd){
        LoginResult loginResult = null ;
        JSONObject result = null;
        String[] paras = {"phone" , "passwd"} ;
        String[] values = {phone, passwd} ;
        result = requestPostHttp(HttpConstant.LOGIN, paras, values, null) ;
        if(result != null){
            Gson gson = new Gson() ;
            loginResult = gson.fromJson(result.toString(), LoginResult.class) ;
        }
        return loginResult ;
    }


    /**
     *零件查询
     * @param number 零件号
     * @return
     */
    public PartOutInfo queryPartByNumber(String number){
        JSONObject result = null;
        PartOutInfo partInfo = null;
        String[] paras = {"number"} ;
        String[] values = {number} ;
        result = requestHttp(HttpConstant.QUERY_PART_NUMBER, paras, values) ;
        if(result != null){
            Gson gson = new Gson();
            partInfo = gson.fromJson(result.toString(), PartOutInfo.class);
        }


        /** {"success":true,"data":{"page":null,"limit":null,"id":13350,"number":"10783549",
         * "name":"蓄电池总成","companyCode":null,"company":"深圳理士奥电源技术有限公司","count":48,
         * "packId":13351,"createTime":"2019-05-18T05:33:59.000+0000","username":null,"status":"1",
         * "pack":{"page":null,"limit":null,"id":13351,"name":"包装数-48件","count":5,"amount":48,"
         * createTime":"2019-05-19T01:58:02.000+0000","status":"1"}},"message":"查询成功","code":0}
         */
        return partInfo ;
    }


    /**
     *
     * @param id
     * @return
     */
    public JSONObject queryPick(String id){
        JSONObject result = null;
        String[] paras = {"id"} ;
        String[] values = {id} ;
        result = requestHttp(HttpConstant.QUERYPICK, paras, values) ;
        return result ;
    }


    /**
     *
     * @param number
     * @param name
     * @param packId
     * @param count
     * @param company
     * @param username
     * @return
     */
    public JSONObject partIn(String number, String name, String packId, int count,
                                String company, String username){
        JSONObject result = null;
        String[] paras = {"number", "name", "packId", "count", "company", "username"} ;
        String[] values = {number, name , packId , count + "" , company , username } ;
        result = requestPostHttp(HttpConstant.PARTIN, paras, values ,new MyShared().getToken(context)) ;
        return result ;
    }

    /**
     *出仓请求
     * @param number 零件号
     * @param name 零件名称
     * @param packId 包装规格
     * @param count 数量
     * @param company 厂商
     * @param username 经办者
     * @return
     */
    public Response partOut(String number, String name, String packId, int count,
                            String company, String username){
        Response response = null ;
        JSONObject result = null;
        String[] paras = {"number", "name", "packId", "count", "company", "username"} ;
        String[] values = {number, name , packId , count + "" , company , username } ;
        result = requestHttp(HttpConstant.PORTOUT, paras, values ) ;
        if(result != null){
            Gson gson = new Gson();
            response = gson.fromJson(result.toString(), Response.class);
        }
        return response ;
    }


    //发送请求
    private JSONObject requestPostHttp(String option ,String[] paras ,String[] value, String token){
        JSONObject json = null ;
        JSONObject paraJsno = new JSONObject() ;
//        //urlStr = urlStr + "?" + HttpConstant.ACTION + "=" + action + "&" + HttpConstant.DATA + "=" + data.toString() ;
//        Logger.e(tag, "url =   " + urlBuffer.toString()); ;

        URL url;
        try {
            url = new URL(URL + option);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            // GET 表示希望从服务器那里获取数据
            //POST 则表示希望提交数据给服务器
            connection.setRequestMethod(HttpConstant.REQUEST_POST);


            connection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            //设置请求头为json
            connection.setRequestProperty("Content-Type","application/json");
            //login不需要token
            if(token != null){
                connection.setRequestProperty("token",token);
            }
            //设置连接超时
            connection.setConnectTimeout(8000);
            //读取超时的毫秒数
            connection.setReadTimeout(8000);
            PrintWriter pw = new PrintWriter(connection.getOutputStream());
            //参数为JSNO格式 如 "{\"phone\":15555555555,\"passwd\":555555}"
            for(int i = 0 ; i < paras.length; i++){
                paraJsno.put(paras[i], value[i]) ;
            }
            pw.print(paraJsno.toString());
            pw.flush();
            pw.close();
            int code = connection.getResponseCode();
            //返回成功
            if(code == 200){
                //获取返回结果
                InputStream is = connection.getInputStream();
                json = toJson(is) ;
                if(json != null){
                    Logger.e(tag, " response =  "+ json.toString() ) ;
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return json  ;

    }

    //发送请求
    private JSONObject requestHttp(String option ,String[] paras ,String[] value){
        JSONObject json = null ;
        //String urlStr = HttpConstant.URL ;

        StringBuffer urlBuffer = new StringBuffer() ;
        urlBuffer.append(URL + option + "?") ;
        urlBuffer.append(paras[0] + "=" + value[0]) ;
        for(int i = 1 ; i < paras.length; i++){
            urlBuffer.append("&" +paras[i] + "=" + value[i]) ;
        }
        //urlStr = urlStr + "?" + HttpConstant.ACTION + "=" + action + "&" + HttpConstant.DATA + "=" + data.toString() ;
        Logger.e(tag, "url =   " + urlBuffer.toString()); ;
        URL url;
        try {
            url = new URL(urlBuffer.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            // GET 表示希望从服务器那里获取数据
            //POST 则表示希望提交数据给服务器
            connection.setRequestMethod(HttpConstant.REQUEST_GET);
            //设置token
            connection.setRequestProperty("token",new MyShared().getToken(context));
            connection.setConnectTimeout(8000); //设置连接超时
            connection.setReadTimeout(8000);    //读取超时的毫秒数
            int code = connection.getResponseCode();
            //返回成功
            if(code == 200){
                //获取返回结果
                InputStream is = connection.getInputStream();
                json = toJson(is) ;
                if(json != null){
                    Logger.e(tag, " response =  "+ json.toString() ) ;
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return json  ;

    }


    //转化为json
    private JSONObject toJson(InputStream is ){
        JSONObject json = null ;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer))!=-1){
                bos.write(buffer, 0, len);
            }
            is.close();
            bos.flush();
            byte[] result = bos.toByteArray();
            String resp = new String(result) ;
            json = new JSONObject(resp) ;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json ;
    }
}
