package com.yiban.hbase.batch;

import lombok.Cleanup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 生成hbase测试数据
 * 100个文件
 * 每个文件100w条数据
 */
public class CreateMyCaseFile {

    public static void main(String[] args) throws ParseException, IOException {
        // 生成hbase测试数据文件
        for (int k = 1; k <= 100; k++) {
            String fileName = "mycase" + k + ".txt";
            File file = new File("D:/tmp", fileName);
            file.createNewFile(); // 创建文件
            //@cleanup实现了closable接口，自动释放资源
            @Cleanup  FileOutputStream in = new FileOutputStream(file);
            @Cleanup OutputStreamWriter osw = new OutputStreamWriter(in, "UTF-8");
            MyCase mycase = new MyCase();
            Random r = new Random();
            long lt = 0;
            Date datetwo = null;
            String start;
            String end;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            byte bt[] = new byte[1024];
            Long start_m = Long.parseLong("1150992000000");
            for (int i = 1000000 * k - 1000000; i <= 1000000 * k; i++) {
                mycase.setC_code("A" + i);
                //生成[0,10)区间的整数
                //假设有13个区
                int qu = r.nextInt(9);
                mycase.setC_rcode(qu + "");
                switch (qu) {
                    case 1:
                        mycase.setC_region("杭州市上城区");
                        break;
                    case 2:
                        mycase.setC_region("杭州市下城区");
                        break;
                    case 3:
                        mycase.setC_region("杭州市拱墅区");
                        break;
                    case 4:
                        mycase.setC_region("杭州市江干区");
                        break;
                    case 5:
                        mycase.setC_region("杭州市西湖区");
                        break;
                    case 6:
                        mycase.setC_region("杭州市滨江区");
                        break;
                    case 7:
                        mycase.setC_region("杭州市萧山区");
                        break;
                    case 8:
                        mycase.setC_region("杭州市余杭区");
                        break;
                    case 0:
                        mycase.setC_region("杭州市其他区");
                        break;
                    default:
                        mycase.setC_region("杭州市其他区");
                        System.out.println(qu + "没有对应的区");
                }
                //假设有3个案件类别
                int c_cate = r.nextInt(4);
                switch (c_cate) {
                    case 0:
                        mycase.setC_cate("刑事案件");
                        break;
                    case 1:
                        mycase.setC_cate("盗窃案件");
                        break;
                    case 2:
                        mycase.setC_cate("强奸案件");
                        break;
                    case 3:
                        mycase.setC_cate("杀人案件");
                        break;
                    default:
                        System.out.println(c_cate + "没有对应的案件类别");
                }

                int day = r.nextInt(5);
                int our = r.nextInt(24);

                start_m = start_m + 86400000 * day;
                String shijiancuo = start_m + "";
                lt = new Long(shijiancuo);
                datetwo = new Date(lt);
                start = simpleDateFormat.format(datetwo);

                Long end_m = start_m + 3600000 * our;
                String shijiancuo2 = end_m + "";
                lt = new Long(shijiancuo2);
                datetwo = new Date(lt);
                end = simpleDateFormat.format(datetwo);

                mycase.setC_start(start);
                mycase.setC_end(end);
                mycase.setC_start_m(start_m);
                mycase.setC_end_m(end_m);

                mycase.setC_name("案件名称" + i);
                mycase.setC_mark("暂无");

                // 向文件写入内容(输出流)
                String str = mycase.toString();

                osw.write(str);
                osw.flush();
//                osw.close();
            }
//            in.close();
        }
    }
}

