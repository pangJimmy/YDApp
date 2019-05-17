package com.pl.ydapp.httpserver;

import com.pl.ydapp.Util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServer {

    private String tag = "HttpServer" ;

    /****
     * 登陆接口
     *
     * @return
     */
    public JSONObject login(String phone, String passwd){
        JSONObject result = null;
        String[] paras = {"phone" , "passwd"} ;
        String[] values = {phone, passwd} ;
        result = requestHttp(HttpConstant.LOGIN, paras, values) ;
        return result ;
    }

    /**
     *
     * @param number
     * @return
     */
    public JSONObject queryPartByNumber(String number){
        JSONObject result = null;
        String[] paras = {"number"} ;
        String[] values = {number} ;
        result = requestHttp(HttpConstant.QUERY_PART_NUMBER, paras, values) ;
        return result ;
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
        result = requestHttp(HttpConstant.PARTIN, paras, values) ;
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
    public JSONObject partOut(String number, String name, String packId, int count,
                              String company, String username){
        JSONObject result = null;
        String[] paras = {"number", "name", "packId", "count", "company", "username"} ;
        String[] values = {number, name , packId , count + "" , company , username } ;
        result = requestHttp(HttpConstant.PORTOUT, paras, values) ;
        return result ;
    }

    private JSONObject requestHttp(String option ,String[] paras ,String[] value){
        JSONObject json = null ;
        //String urlStr = HttpConstant.URL ;

        StringBuffer urlBuffer = new StringBuffer() ;
        urlBuffer.append(HttpConstant.URL+ option + "?") ;
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
            // TODO Auto-generated catch block
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
