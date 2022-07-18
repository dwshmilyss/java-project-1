package com.yiban.javaBase.dev.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2022/2/14
 * @website http://blog.csdn.net/dwshmilyss
 */
public class HttpPostDemo {

    public static final Logger logger = LoggerFactory.getLogger(HttpPostDemo.class);
    public static final String COOKIE_SESSION_ID_KEY = "sessionid";
    private static final String loginUrl = "http://127.0.0.1:8000/hue/accounts/login";
    private static final String loginUser = "1";
    private static final String loginPsw = "Sjtu403c@#%";
    private static CookieStore cookieStore;

    public static void main(String[] args) {
        String sessionId = loginAndGetSessionId(loginUrl, loginUser, loginPsw);
        System.out.println("sessionId = " + sessionId);
//        postWithForm();
//        locationPrestoPage(sessionId);
//        createNewUser(sessionId, "1", "123");
//        deleteNewUser(sessionId, "1");
//        logout();
//        System.out.println("sessionId = " + sessionId);

    }


    private static String loginAndGetSessionId(String url, String user, String psw) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        //Create a parameter queue
        List formParams = new ArrayList();
        formParams.add(new BasicNameValuePair("username", user));
        formParams.add(new BasicNameValuePair("password", psw));
        String sessionId = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            // 状态码为：200
            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                //Get Cookies
                cookieStore = httpClient.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();
                for (Cookie cookie : cookies) {
                    System.out.println("cookie.name = " + cookie.getName() + ",cookie.value = " + cookie.getValue());
                    if (COOKIE_SESSION_ID_KEY.equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            } else {
                String resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.error("resultData = " + resultData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionId;
    }

    public static void postWithForm() {
        // 用于接收返回的结果
        String resultData = "";
//    DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            String url = "http://127.0.0.1:8000/hue/accounts/login";
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "'application/x-www-form-urlencoded'");
            post.setHeader("Referer", "http://127.0.0.1:8000/hue/accounts/login?next=/");
            post.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            pairList.add(new BasicNameValuePair("csrfmiddlewaretoken", "7jfqEetOx7Z7dDn0S1wfZDPTkOSdTox92Hle9AzKrUCPuSmBZeKw1XtYbHBQSNOZ"));
            pairList.add(new BasicNameValuePair("username", "1"));
            pairList.add(new BasicNameValuePair("password", "123"));
            pairList.add(new BasicNameValuePair("next", "/"));
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList, "utf-8");
            post.setEntity(uefe);
            // 创建一个http客户端
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            // 发送post请求
            CloseableHttpResponse response = httpClient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            // 状态码为：200
            if (statusCode == HttpStatus.SC_OK) {
                // 返回数据：
                resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.error("resultData = " + resultData);
            } else {
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
    }

    public static void locationPrestoPage(String sessionId) {
        System.out.println("get sessionId = " + sessionId);
        // 用于接收返回的结果
        String resultData = "";
        try {
            String url = "http://127.0.0.1:8000/hue/editor/?type=presto";
            HttpPost httpPost = new HttpPost(url);
            //第一种携带cookie的方式
//      HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

            //第二种携带cookie的方式
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            if (sessionId != null && !"".equals(sessionId)) {
                httpPost.addHeader(new BasicHeader("Cookie", COOKIE_SESSION_ID_KEY + "=" + sessionId));
            }

            //发送请求
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            // 状态码为：200
            if (statusCode == HttpStatus.SC_OK) {
                // 返回数据：
                resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("resultData = " + resultData);
            } else {
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
    }

    public static void createNewUser(String sessionId, String username, String password) {
        System.out.println("get sessionId = " + sessionId);
        // 用于接收返回的结果
        String resultData = "";
        try {
            String url = "http://127.0.0.1:8000/useradmin/users/new";
            HttpPost httpPost = new HttpPost(url);
            //第一种携带cookie的方式
//      HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

            //第二种携带cookie的方式
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            if (sessionId != null && !"".equals(sessionId)) {
                httpPost.addHeader(new BasicHeader("Cookie", COOKIE_SESSION_ID_KEY + "=" + sessionId));
            }
//            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//            httpPost.addHeader("Referer", "http://127.0.0.1:8000/hue/useradmin/users/new");

            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            pairList.add(new BasicNameValuePair("username", username));
            pairList.add(new BasicNameValuePair("password1", password));
            pairList.add(new BasicNameValuePair("password2", password));
            pairList.add(new BasicNameValuePair("ensure_home_directory", "on"));
            pairList.add(new BasicNameValuePair("first_name", ""));
            pairList.add(new BasicNameValuePair("last_name", ""));
            pairList.add(new BasicNameValuePair("email", ""));
            pairList.add(new BasicNameValuePair("groups", "1"));
            pairList.add(new BasicNameValuePair("is_active", "on"));
            pairList.add(new BasicNameValuePair("is_embeddable", "true"));
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList, "utf-8");
            httpPost.setEntity(uefe);

            //发送请求
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            // 状态码为：200
            if (statusCode == HttpStatus.SC_OK) {
                // 返回数据：
                resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("resultData = " + resultData);
            } else {
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
    }

    public static void deleteNewUser(String sessionId, String username) {
        //TODO jdbc查询Mysql->hue->auth_user
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hue", "root", "120653");
            String sql = "select id from auth_user where username = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            // 展开结果集数据库
            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (rs != null) pstmt.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception se) {
                se.printStackTrace();
            }
        }
        System.out.println("get id = " + id);
        System.out.println("get sessionId = " + sessionId);
        // 用于接收返回的结果
        String resultData = "";
        try {
            String url = "http://127.0.0.1:8000/useradmin/users/delete";
            HttpPost httpPost = new HttpPost(url);
            //第一种携带cookie的方式
//      HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            //第二种携带cookie的方式
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            if (sessionId != null && !"".equals(sessionId)) {
                httpPost.addHeader(new BasicHeader("Cookie", COOKIE_SESSION_ID_KEY + "=" + sessionId));
            }
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.addHeader("Referer", "http://127.0.0.1:8000/hue/useradmin/users/delete");

            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            pairList.add(new BasicNameValuePair("user_ids", String.valueOf(id)));
            pairList.add(new BasicNameValuePair("is_delete", "on"));
            pairList.add(new BasicNameValuePair("is_embeddable", "true"));
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList, "utf-8");
            httpPost.setEntity(uefe);

            //发送请求
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            // 状态码为：200
            if (statusCode == HttpStatus.SC_OK) {
                // 返回数据：
                resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("resultData = " + resultData);
            } else {
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
    }

    private static void logout() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8000/hue/accounts/logout");
        //Create a parameter queue
        List formParams = new ArrayList();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
