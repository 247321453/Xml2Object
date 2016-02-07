/**
 *
 */
package com.uutils.xml2object;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class OnlineUtils {
     //region network
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();// wifi
        return info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileConnected(Context context) {
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }
    //endregion

    //region http
    public static String toString(Map<String, String> list) {
        if (list == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> e : list.entrySet()) {
            sb.append(e.getKey() + "=" + Uri.encode(e.getValue(), "UTF-8"));
            sb.append("&");
        }
        String args = sb.toString();
        if (args.endsWith("&")) {
            args = args.substring(0, args.length() - 1);
        }
        return args;
    }
    /***
     * @param url
     * @param ispost
     * @param timeout
     * @param propertys 头
     * @param datas     body
     * @return
     * @throws IOException
     */
    public static HttpURLConnection connect(
            String url, boolean ispost, int timeout,
            Map<String, String> datas, Map<String, String> propertys)
            throws IOException {
        URL _url = new URL(url);
        HttpURLConnection url_con = (HttpURLConnection) _url.openConnection();
        if (timeout > 0) {
            url_con.setConnectTimeout(timeout);
            url_con.setReadTimeout(timeout);
        }
        url_con.setRequestProperty("User-agent", System.getProperty("http.agent"));
        if (propertys != null) {
            for (Map.Entry<String, String> entry : propertys.entrySet()) {
                url_con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        // 设置session
        if (ispost) {
            // 输入参数
            url_con.setRequestMethod("POST");
            String body = toString(datas);
            if (body != null && body.length() > 0) {
                url_con.setDoOutput(true);
                url_con.getOutputStream().write(body.getBytes());
            }
        } else {
            url_con.setRequestMethod("GET");
        }
        return url_con;
    }

    private static byte[] getContent(String url, boolean ispost, int timeout, Map<String, String> datas) {
        ByteArrayOutputStream outputStream = null;
        InputStream inputStream = null;
        byte[] result = null;
        HttpURLConnection conn = null;
        try {
            conn = connect(url, ispost, 60 * 1000, datas, null);
            outputStream = new ByteArrayOutputStream();
            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
                inputStream = conn.getInputStream();
                byte[] data = new byte[4096];
                int len = 0;
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = outputStream.toByteArray();
            } else if (code < HttpURLConnection.HTTP_OK) {
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
    static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static byte[] get(String url) {
        return getContent(url, false, 60 * 1000, null);
    }

    // 使用POST方法提交到后台
    public static byte[] post(String url, Map<String, String> data) {
        return getContent(url, true, 60 * 1000, data);
    }
    //endregion
}
