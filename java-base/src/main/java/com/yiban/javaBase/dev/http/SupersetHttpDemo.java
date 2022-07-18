package com.yiban.javaBase.dev.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2022/2/14
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SupersetHttpDemo {

    public static final Logger logger = LoggerFactory.getLogger(SupersetHttpDemo.class);
    public static final String COOKIE_SESSION_ID_KEY = "session";
    //    private static final String apiUrl = "http://127.0.0.1:9000/api/v1";
    private static final String addr = "http://127.0.0.1:9000";
    private static final String loginUser = "admin";
    private static final String loginPsw = "123";
    private static String accessToken;
    private static String refreshToken;
    private static CookieStore cookieStore;

    public static void main(String[] args) {
//        String sessionId = getCsrfToken("/security/csrf_token/");
        String csrfToken = getCsrfToken("/login/");
        login("/login/", loginUser, loginPsw, csrfToken);
//        String sessionId = loginWithApi("/login/", loginUser, loginPsw);
        createNewUser("/users/add","dww","123","dww@163.com",csrfToken);
//        System.out.println("sessionId = " + sessionId);
//        postWithForm();
//        locationPrestoPage(sessionId);
//        createNewUser(sessionId, "1", "123");
//        deleteNewUser(sessionId, "1");
//        logout();
//        System.out.println("sessionId = " + sessionId);

    }

    private static String getCsrfToken(String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(addr + url);
        CloseableHttpResponse response = null;
        String csrf_token = "";
        try {
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            String resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
            // 状态码为：200
            if (statusCode == HttpStatus.SC_OK) {
                Document document = Jsoup.parse(resultData);
                csrf_token = document.getElementById("csrf_token").val();
                System.out.println("csrf_token = " + csrf_token);
            } else {
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
        return csrf_token;
    }

    private static void login(String url, String username, String password, String csrfToken) {
//        CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        String loginUrl = addr + url;
        HttpPost httpPost = new HttpPost(loginUrl);
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        try {
            httpPost.setConfig(requestConfig);
            //Create a parameter queue
            List formParams = new ArrayList();
            formParams.add(new BasicNameValuePair("username", username));
            formParams.add(new BasicNameValuePair("password", password));
            formParams.add(new BasicNameValuePair("csrf_token", csrfToken));
            httpPost.setHeader("X-CSRFToken", csrfToken);
            httpPost.setHeader("Referer", loginUrl);
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("login statusCode = " + statusCode);
            String resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
            // 状态码为：302
            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                logger.info("login success");
                logger.info("resultData = " + resultData);
            } else {
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
    }

    private static String loginWithApi(String uri, String user, String psw) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpPost httpPost = new HttpPost(addr + uri);
        RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
        //Create a parameter queue
        String sessionId = null;
        CloseableHttpResponse response = null;
        try {
            httpPost.setHeader("Content-Type", "application/json");
            String data = "{\n" +
                    "  \"password\": \"" + psw + "\",\n" +
                    "  \"username\": \"" + user + "\"\n" +
                    "}";
            StringEntity stringEntity = new StringEntity(data,
                    ContentType.create("text/json", "UTF-8"));
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("statusCode = " + statusCode);
            // 状态码为：302
            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                //Get Cookies
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

    public static void createNewUser(String uri, String username, String password, String email,String csrfToken) {
        String resultData = "";
        try {
//            CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
            RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(false).build();
            CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost httpPost = new HttpPost(addr + uri);
            httpPost.setConfig(requestConfig);
            String loginUrl = addr + "/login/";
            httpPost.setHeader("X-CSRFToken", csrfToken);
            httpPost.setHeader("Referer", loginUrl);
            List formParams = new ArrayList();
            formParams.add(new BasicNameValuePair("first_name", username));
            formParams.add(new BasicNameValuePair("last_name", username));
            formParams.add(new BasicNameValuePair("username", username));
            formParams.add(new BasicNameValuePair("email", email));
            formParams.add(new BasicNameValuePair("active", "y"));
            formParams.add(new BasicNameValuePair("password", password));
            formParams.add(new BasicNameValuePair("conf_password", password));
            formParams.add(new BasicNameValuePair("roles", "4"));
            formParams.add(new BasicNameValuePair("csrf_token", csrfToken));
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));

//            String data = "{\n" +
//                    "  \"first_name\": \"" + username + "\",\n" +
//                    "  \"last_name\": \"" + username + "\",\n" +
//                    "  \"username\": \"" + username + "\",\n" +
//                    "  \"email\": \"" + email + "\",\n" +
//                    "  \"active\": \"y\",\n" +
//                    "  \"password\": \"" + password + "\",\n" +
//                    "  \"conf_password\": \"" + password + "\",\n" +
//                    "  \"roles\": \"4\"\n" +
//                    "}";
//            StringEntity stringEntity = new StringEntity(data,
//                    ContentType.create("text/json", "UTF-8"));
//            httpPost.setEntity(stringEntity);
            //发送请求
            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("createNewUser statusCode = " + statusCode);
            // 状态码为：200
            resultData = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (statusCode == HttpStatus.SC_OK) {
                // 返回数据：
                System.out.println("createNewUser success,resultData = " + resultData);
            } else {
                System.out.println("createNewUser failure,resultData = " + resultData);
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
