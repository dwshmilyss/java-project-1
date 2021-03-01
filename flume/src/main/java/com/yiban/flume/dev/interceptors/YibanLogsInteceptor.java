package com.yiban.flume.dev.interceptors;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.sink.LoggerSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @auther WEI.DUAN
 * @date 2018/8/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class YibanLogsInteceptor implements Interceptor {
    private static final Logger logger = LoggerFactory
            .getLogger(YibanLogsInteceptor.class);

    public YibanLogsInteceptor() {
    }

    public void initialize() {

    }

    private ThreadLocal<SimpleDateFormat> formatCache = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH");
        }
    };

    public Event intercept(Event event) {
        if (event == null) {
            return null;
        }
        Map<String, String> headers = event.getHeaders();
        String bodyStr = new String(event.getBody(), Charsets.UTF_8);
        System.out.println("body:" + bodyStr.toString());
        if (bodyStr.contains("\"dst_obj\":[]") || bodyStr.contains("\"dst_obj\":\"\"")) {
            bodyStr = bodyStr.replace("\"dst_obj\":[]", "\"dst_obj\":{}").replace("\"dst_obj\":\"\"","\"dst_obj\":{}");
        }
        StringBuffer bodyStringBuffer = new StringBuffer();
        try { //解析JSON
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            String product = jsonObject.getString("product");
            String platform = jsonObject.getString("platform");
            String module = jsonObject.getString("module");
            String action = jsonObject.getString("action");
            String description = jsonObject.getString("description");
            String ip = jsonObject.getString("ip");
            long timestamp = jsonObject.getLong("time") * 1000l;
            String yyyyMMdd = formatCache.get().format(new Date(timestamp)).split(" ")[0];
            String hour = formatCache.get().format(new Date(timestamp)).split(" ")[1];
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(timestamp));
//            System.out.println("yyyyMMdd = " + yyyyMMdd);
//            System.out.println("year = " + calendar.get(Calendar.YEAR));
//            System.out.println("month = " + calendar.get(Calendar.MONTH)+1);
//            System.out.println("day = " + calendar.get(Calendar.DATE));
            headers.put("year", calendar.get(Calendar.YEAR) + "");
            headers.put("month", calendar.get(Calendar.MONTH) + 1 + "");
            headers.put("day", calendar.get(Calendar.DATE) + "");
            headers.put("product", module);
            JSONObject srcJsonObject = jsonObject.getJSONObject("src_obj");
            JSONObject dstJsonObject = jsonObject.getJSONObject("dst_obj");
            bodyStringBuffer.append(platform).append("\t").append(product).append("\t").append(module).append("\t")
                    .append(action).append("\t").append(description).append("\t").append(ip).append("\t")
                    .append(yyyyMMdd).append("\t").append(hour).append("\t");
            if ("hotAPP".equals(module)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String srcUrl = srcJsonObject.getString("url") == null ? "" : srcJsonObject.getString("url");
                String content = srcJsonObject.getString("content") == null ? "" : srcJsonObject.getString("content");
                String schoolId = srcJsonObject.getString("school_id") == null ? "" : srcJsonObject.getString("school_id");
                String position = dstJsonObject.getString("position") == null ? "" : dstJsonObject.getString("position");
                bodyStringBuffer.append(srcUserId).append("\t").append(srcUrl).append("\t").append(content).append("\t")
                        .append(position).append("\t").append(schoolId);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("vote".equalsIgnoreCase(product)) {
                //src_obj
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String voteTitle = srcJsonObject.getString("vote_title") == null ? "" : srcJsonObject.getString("vote_title");
                String voteUrl = srcJsonObject.getString("vote_url") == null ? "" : srcJsonObject.getString("vote_url");
                String commentLevel = srcJsonObject.getString("comment_level") == null ? "" : srcJsonObject.getString("comment_level");
                String commentContent = srcJsonObject.getString("comment_content") == null ? "" : srcJsonObject.getString("comment_content");
                //dst_obj
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");
                String position = dstJsonObject.getString("position") == null ? "" : dstJsonObject.getString("position");
                String gid = dstJsonObject.getString("gid") == null ? "" : dstJsonObject.getString("gid");
                String guid = dstJsonObject.getString("guid") == null ? "" : dstJsonObject.getString("guid");
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String oid = dstJsonObject.getString("oid") == null ? "" : dstJsonObject.getString("oid");
                bodyStringBuffer.append(srcUserId).append("\t").append(voteTitle).append("\t").append(voteUrl).append("\t").append(commentLevel).append("\t")
                        .append(commentContent).append("\t").append(dstUserId).append("\t").append(position).append("\t").append(gid).append("\t").append(guid)
                        .append("\t").append(operate_obj).append("\t").append(oid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("forum".equalsIgnoreCase(product)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String forumTitle = srcJsonObject.getString("forum_title") == null ? "" : srcJsonObject.getString("forum_title");
                String forumUrl = srcJsonObject.getString("forum_url") == null ? "" : srcJsonObject.getString("forum_url");
                String commentLevel = srcJsonObject.getString("comment_level") == null ? "" : srcJsonObject.getString("comment_level");
                String commentContent = srcJsonObject.getString("comment_content") == null ? "" : srcJsonObject.getString("comment_content");

                //dst_obj
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");
                String position = dstJsonObject.getString("position") == null ? "" : dstJsonObject.getString("position");
                String gid = dstJsonObject.getString("gid") == null ? "" : dstJsonObject.getString("gid");
                String guid = dstJsonObject.getString("guid") == null ? "" : dstJsonObject.getString("guid");
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String oid = dstJsonObject.getString("oid") == null ? "" : dstJsonObject.getString("oid");
                String channelId = dstJsonObject.getString("channel_id") == null ? "" : dstJsonObject.getString("channel_id");
                bodyStringBuffer.append(srcUserId).append("\t").append(forumTitle).append("\t").append(forumUrl).append("\t").append(commentLevel).append("\t")
                        .append(commentContent).append("\t").append(dstUserId).append("\t").append(position).append("\t").append(gid).append("\t").append(guid)
                        .append("\t").append(operate_obj).append("\t").append(oid).append("\t").append(channelId);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("blog".equalsIgnoreCase(product)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String blogTitle = srcJsonObject.getString("blog_title") == null ? "" : srcJsonObject.getString("blog_title");
                String blogUrl = srcJsonObject.getString("blog_url") == null ? "" : srcJsonObject.getString("blog_url");
                String commentLevel = srcJsonObject.getString("comment_level") == null ? "" : srcJsonObject.getString("comment_level");
                String commentContent = srcJsonObject.getString("comment_content") == null ? "" : srcJsonObject.getString("comment_content");

                //dst_obj
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String oid = dstJsonObject.getString("oid") == null ? "" : dstJsonObject.getString("oid");
                bodyStringBuffer.append(srcUserId).append("\t").append(blogTitle).append("\t").append(blogUrl).append("\t").append(commentLevel).append("\t")
                        .append(commentContent).append("\t").append(dstUserId).append("\t").append(operate_obj).append("\t").append(oid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("file".equalsIgnoreCase(product)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String fieName = srcJsonObject.getString("file_name") == null ? "" : srcJsonObject.getString("file_name");
                String fileUrl = srcJsonObject.getString("file_url") == null ? "" : srcJsonObject.getString("file_url");
                String dirName = srcJsonObject.getString("dir_name") == null ? "" : srcJsonObject.getString("dir_name");
                String dirUrl = srcJsonObject.getString("dir_url") == null ? "" : srcJsonObject.getString("dir_url");

                //dst_obj
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String position = dstJsonObject.getString("position") == null ? "" : dstJsonObject.getString("position");
                String gid = dstJsonObject.getString("gid") == null ? "" : dstJsonObject.getString("gid");
                String guid = dstJsonObject.getString("guid") == null ? "" : dstJsonObject.getString("guid");
                bodyStringBuffer.append(srcUserId).append("\t").append(fieName).append("\t").append(fileUrl).append("\t").append(dirName).append("\t").append(dirUrl).append("\t")
                        .append(dstUserId).append("\t").append(operate_obj).append("\t").append(position).append("\t").append(gid).append("\t").append(guid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("fastBuild".equalsIgnoreCase(product)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String title = srcJsonObject.getString("title") == null ? "" : srcJsonObject.getString("title");
                String url = srcJsonObject.getString("url") == null ? "" : srcJsonObject.getString("url");

                //dst_obj
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String position = dstJsonObject.getString("position") == null ? "" : dstJsonObject.getString("position");
                String oid = dstJsonObject.getString("oid") == null ? "" : dstJsonObject.getString("oid");
                bodyStringBuffer.append(srcUserId).append("\t").append(title).append("\t").append(url).append("\t").append(operate_obj).append("\t").append(position).append("\t").append(oid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("questionnaire".equalsIgnoreCase(product)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String title = srcJsonObject.getString("title") == null ? "" : srcJsonObject.getString("title");
                String url = srcJsonObject.getString("url") == null ? "" : srcJsonObject.getString("url");

                //dst_obj
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String oid = dstJsonObject.getString("oid") == null ? "" : dstJsonObject.getString("oid");
                bodyStringBuffer.append(srcUserId).append("\t").append(title).append("\t").append(url).append("\t").append(operate_obj).append("\t").append(oid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("esport".equalsIgnoreCase(product)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                bodyStringBuffer.append(srcUserId);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("message".equalsIgnoreCase(module)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String sendTxt = srcJsonObject.getString("send_txt") == null ? "" : srcJsonObject.getString("send_txt");

                //dst_obj
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");
                String position = dstJsonObject.getString("position") == null ? "" : dstJsonObject.getString("position");
                String gid = dstJsonObject.getString("gid") == null ? "" : dstJsonObject.getString("gid");
                String guid = dstJsonObject.getString("guid") == null ? "" : dstJsonObject.getString("guid");
                bodyStringBuffer.append(srcUserId).append("\t").append(sendTxt).append("\t").append(dstUserId).append("\t").append(operate_obj).append("\t").append(position).append("\t").append(gid).append("\t").append(guid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("user".equalsIgnoreCase(module)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String cli_ver = srcJsonObject.getString("cli_ver") == null ? "" : srcJsonObject.getString("cli_ver");
                String latest_cli_ver = srcJsonObject.getString("latest_cli_ver") == null ? "" : srcJsonObject.getString("latest_cli_ver");
                String is_old = srcJsonObject.getString("is_old") == null ? "" : srcJsonObject.getString("is_old");
                String device_number = srcJsonObject.getString("device_number") == null ? "" : srcJsonObject.getString("device_number");
                String content = srcJsonObject.getString("content") == null ? "" : srcJsonObject.getString("content");
                String school_id = srcJsonObject.getString("school_id") == null ? "" : srcJsonObject.getString("school_id");
                String reset = srcJsonObject.getString("reset") == null ? "" : srcJsonObject.getString("reset");
                String appName = srcJsonObject.getString("appName") == null ? "" : srcJsonObject.getString("appName");
                String appUrl = srcJsonObject.getString("appUrl") == null ? "" : srcJsonObject.getString("appUrl");

                //dst_obj
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");

                bodyStringBuffer.append(srcUserId).append("\t").append(cli_ver).append("\t").append(latest_cli_ver).append("\t").append(is_old).append("\t")
                        .append(device_number).append("\t").append(content).append("\t").append(school_id).append("\t").append(reset).append("\t").append(appName).append("\t")
                        .append(appUrl).append("\t").append(operate_obj).append("\t").append(dstUserId);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("feeds".equalsIgnoreCase(module)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String content = srcJsonObject.getString("content") == null ? "" : srcJsonObject.getString("content");
                String url = srcJsonObject.getString("url") == null ? "" : srcJsonObject.getString("url");
                String commentLevel = srcJsonObject.getString("comment_level") == null ? "" : srcJsonObject.getString("comment_level");

                //dst_obj
                String dstUserId = dstJsonObject.getString("uid") == null ? "" : dstJsonObject.getString("uid");
                String gid = dstJsonObject.getString("gid") == null ? "" : dstJsonObject.getString("gid");
                String aid = dstJsonObject.getString("aid") == null ? "" : dstJsonObject.getString("aid");
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                String oid = dstJsonObject.getString("oid") == null ? "" : dstJsonObject.getString("oid");
                bodyStringBuffer.append(srcUserId).append("\t").append(content).append("\t").append(url).append("\t").append(commentLevel).append("\t")
                        .append(dstUserId).append("\t").append(gid).append("\t").append(aid).append("\t").append(operate_obj).append("\t").append(oid);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else if ("friend".equalsIgnoreCase(module)) {
                String srcUserId = srcJsonObject.getString("uid") == null ? "" : srcJsonObject.getString("uid");
                String operate_obj = dstJsonObject.getString("operate_obj") == null ? "" : dstJsonObject.getString("operate_obj");
                bodyStringBuffer.append(srcUserId).append("\t").append(operate_obj);
                event.setBody(bodyStringBuffer.toString().getBytes());
                return event;
            } else {
                return null;
            }

        } catch (Exception e) {//如果解析失败，那么直接按原格式输出，并且设置header的key为error，以便将出错的数据输出到指定目录，便于后期排查
//            headers.put("year", "error");
//            headers.put("month", "error");
//            headers.put("day", "error");
//            headers.put("module", "error");
            e.printStackTrace();
            logger.error(e.getMessage() + bodyStr);
            return null;
        }
    }

    public List<Event> intercept(List<Event> events) {
        List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
        for (Event event : events) {
            Event interceptedEvent = intercept(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;
    }

    public void close() {

    }


    public static class Builder implements Interceptor.Builder {
        //使用Builder初始化Interceptor
        public Interceptor build() {
            return new YibanLogsInteceptor();
        }

        public void configure(Context context) {

        }
    }
}
