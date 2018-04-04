package com.xmd.cashier.common;

import android.text.TextUtils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by BinGe on 2017/9/13.
 * http 功能类
 */

public final class Http {

    /**
     * 返回请求结果数据
     */
    public final static class ResponseData {

        /**
         * 类只能由http工具创建
         */
        private ResponseData() {

        }

        public JError error;

        public String cookies;

        public String responseMsg;

        public int status;

        public boolean isResponseOK() {
            return 200 == status && error == null;
        }

    }

    /**
     * Post服务请求
     * @param requestUrl 请求地址
     * @param params     请求参数
     * @return
     */
    public static ResponseData post(String requestUrl, Map<String, Object> params) {
        return post(null, requestUrl, params);
    }
    public static ResponseData post(String cookie, String requestUrl, Map<String, Object> params) {
        StringBuffer requestParams = new StringBuffer();
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                requestParams.append(key);
                requestParams.append("=");
                requestParams.append(value);
                requestParams.append("&");
            }
            if (requestParams.length() > 0) {
                requestParams.deleteCharAt(requestParams.length() - 1);
            }
        }
        return post(cookie, requestUrl, requestParams.toString());
    }

    /**
     * Post服务请求
     *
     * @param requestUrl 请求地址
     * @param params     请求参数
     * @return
     */
    public static ResponseData post(String requestUrl, String params) {
        return post(null, requestUrl, params);
    }
    public static ResponseData post(String cookie, String requestUrl, String params) {
        ResponseData data = new ResponseData();
        try {
            //建立连接
            if (TextUtils.isEmpty(requestUrl)) {
                data.error = new JError(JError.DEFAULT_ERROR_CODE, "URL is Empty!", requestUrl);
                data.status = JError.DEFAULT_ERROR_CODE;
                return data;
            }
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //设置连接属性
            connection.setDoOutput(true); //使用URL连接进行输出
            connection.setDoInput(true); //使用URL连接进行输入
            connection.setUseCaches(false); //忽略缓存
            connection.setRequestMethod("POST"); //设置URL请求方法
            connection.setRequestProperty("Cookie", cookie);

            //设置请求属性
            byte[] requestStringBytes = params.getBytes(); //获取数据字节数据
            connection.setRequestProperty("Content-length", "" + requestStringBytes.length);
            connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            //建立输出流,并写入数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.close();

            //获取cookie
            String cookieVal = connection.getHeaderField("Set-Cookie");
            data.cookies = cookieVal;

            //获取响应状态
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) { //连接成功
                //当正确响应时处理数据
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                //处理响应流
                responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                data.status = responseCode;
                data.responseMsg = buffer.toString();
                return data;
            } else {
                JError err = new JError(responseCode, connection.getResponseMessage(), requestUrl);
                data.error = err;
                data.status = responseCode;
                return data;
            }

        } catch (Exception e) {
            data.error = new JError(JError.DEFAULT_ERROR_CODE, e.getMessage(), requestUrl);
            data.status = JError.DEFAULT_ERROR_CODE;
            return data;
        }
    }

    /**
     * Get服务请求
     *
     * @param requestUrl
     * @return
     */
    public static String sendGet(String requestUrl) {
        try {
            //建立连接
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            connection.connect();

            //获取响应状态
            int responseCode = connection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) { //连接成功
                //当正确响应时处理数据
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                //处理响应流
                responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                //JSONObject result = new JSONObject(buffer.toString());
                return buffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param cookie
     * @param file      要上传的文件
     * @param fileKey   文件对应的key如：image，picture
     * @param uploadUrl 上传的url地址
     * @param param     附带的参数
     * @return
     */
   public static ResponseData uploadImage(String cookie, File file, String fileKey, String uploadUrl, Map<String, String> param) {
        ResponseData data = new ResponseData();
        final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        final String PREFIX = "--";
        final String LINE_END = "\r\n";
        final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String result;
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (!TextUtils.isEmpty(cookie)) {
                conn.setRequestProperty("Cookie", cookie);
            }

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //以下是用于上传参数
            if (param != null && param.size() > 0) {
                Iterator<String> it = param.keySet().iterator();
                while (it.hasNext()) {
                    StringBuffer sb = new StringBuffer();
                    String key = it.next();
                    String value = param.get(key);
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                    sb.append(value).append(LINE_END);
                    dos.write(sb.toString().getBytes());
                }
            }
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition:form-data; name=\"" + fileKey + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
            sb.append("Content-Type:image/jpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            /**上传文件*/
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            int curLen = 0;
            while ((len = is.read(bytes)) != -1) {
                curLen += len;
                dos.write(bytes, 0, len);
            }
            is.close();

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();

            //获取cookie
            String cookieVal = conn.getHeaderField("Set-Cookie");
            data.cookies = cookieVal;

            int res = conn.getResponseCode();
            if (res == 200) {
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                data.responseMsg = result;
                data.status = res;
            } else {
                JError err = new JError(res, conn.getResponseMessage(), uploadUrl);
                data.error = err;
                data.status = res;
            }
        } catch (Exception e) {
            JError err = new JError(JError.DEFAULT_ERROR_CODE, e.getMessage(), uploadUrl);
            data.error = err;
            data.status = JError.DEFAULT_ERROR_CODE;
        }
        return data;
    }

}
