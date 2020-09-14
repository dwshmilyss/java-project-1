package com.yiban.flume.dev.interceptors;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @auther WEI.DUAN
 * @date 2018/8/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LogAnalysis implements Interceptor {
    public LogAnalysis() {
    }

    public void initialize() {

    }

    public Event intercept(Event event) {
        String body = new String(event.getBody(), Charsets.UTF_8);
        System.out.println("body:"+body.toString());
        //String line = "2016-04-18 16:00:00 {\"areacode\":\"浙江省丽水市\",\"countAll\":0,\"countCorrect\":0,\"datatime\":\"4134362\",\"logid\":\"201604181600001184409476\",\"requestinfo\":\"{\\\"sign\\\":\\\"4\\\",\\\"timestamp\\\":\\\"1460966390499\\\",\\\"remark\\\":\\\"4\\\",\\\"subjectPro\\\":\\\"123456\\\",\\\"interfaceUserName\\\":\\\"12345678900987654321\\\",\\\"channelno\\\":\\\"100\\\",\\\"imei\\\":\\\"12345678900987654321\\\",\\\"subjectNum\\\":\\\"13989589062\\\",\\\"imsi\\\":\\\"12345678900987654321\\\",\\\"queryNum\\\":\\\"13989589062\\\"}\",\"requestip\":\"36.16.128.234\",\"requesttime\":\"2016-04-18 16:59:59\",\"requesttype\":\"0\",\"responsecode\":\"010005\",\"responsedata\":\"无查询结果\"}\n";
        String pattern1 = "\"areacode\":\"[\\u4e00-\\u9fa5]*"; //汉字正则表达式
        String pattern2 = "\"datatime\":\"[0-9]*"; //数字正则表达式
        String pattern3 = "\\\\\"imei\\\\\":\\\\\"[0-9]*"; //时间正则表达式  \\\\\"imei\\\\\":\\\\\"
        String pattern4 = "\"requestip\":\"[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}"; //ip正则表达式
        String pattern5 = "\"requesttime\":\"((19|20)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) ([012][0-9]):([0-5][0-9]):([0-5][0-9])"; //"requesttime":"2016-04-18 16:00:00
        //String pattern = "\"areacode\":\"[^0-9a-z]*\",";
        // 创建 Pattern 对象
        Pattern r1 = Pattern.compile(pattern1);
        Pattern r2 = Pattern.compile(pattern2);
        Pattern r3 = Pattern.compile(pattern3);
        Pattern r4 = Pattern.compile(pattern4);
        Pattern r5 = Pattern.compile(pattern5);
        // 现在创建 matcher 对象
        Matcher m1 = r1.matcher(body);
        Matcher m2 = r2.matcher(body);
        Matcher m3 = r3.matcher(body);
        Matcher m4 = r4.matcher(body);
        Matcher m5 = r5.matcher(body);
        StringBuffer bodyoutput = new StringBuffer();
        if (m1.find() && m2.find() && m3.find() && m4.find() && m5.find()) {

            bodyoutput = bodyoutput.append(m1.group(0)+("|")+m2.group(0)+"|"+m3.group(0)+"|"+m4.group(0)+"|"+m5.group(0));


        } else {
            bodyoutput = bodyoutput.append("No match!!!");

        }

        //System.out.println("result:"+JsonUtil.ObjectToJsonString(report));
        event.setBody(bodyoutput.toString().getBytes());
        return event;
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
            return new LogAnalysis();
        }

        public void configure(Context context) {

        }
    }
}
