package com.yiban.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 模糊匹配+分页
 *
 * @auther WEI.DUAN
 * @date 2019/3/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class PageFilterUtil {
    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(PageFilter.class);


    /**
     * 获取指定Rowkey正则的数据列表（分页）
     *
     * @param pageSize   页大小
     * @param lastRowKey 上一页最后的rowkey
     * @param rowkeyReg  Rowkey正则
     * @return 数据列表
     */
    public List<Object> getData(int pageSize, String lastRowKey, String rowkeyReg) throws IOException {
        List<Object> dataList = new ArrayList<>();
        Connection conn = HbaseNewAPI.getConnection();
        try {
            // 二级索引表查询索引数据
            Table table = conn.getTable(TableName.valueOf(""));
            Scan scan = new Scan();
            // 构建模糊查询的Filter和分页的Filter
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            if (rowkeyReg != null) {
                RegexStringComparator regex = new RegexStringComparator(rowkeyReg);
                Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, regex);
                filterList.addFilter(filter);
            }
            Filter pageFilter = new PageFilter(pageSize);
            Filter rowFilter = new RowFilter(CompareFilter.CompareOp.GREATER,
                    new BinaryComparator(Bytes.toBytes(lastRowKey)));
            filterList.addFilter(pageFilter);
            filterList.addFilter(rowFilter);
            scan.setFilter(filterList);
            ResultScanner rs = table.getScanner(scan);
            Result result;
            int rowNum = 0;
            while ((result = rs.next()) != null) {
                if (rowNum >= pageSize) {
                    break;
                }
                List<Cell> listCells = result.listCells();
                for (Cell cell : listCells) {
                    String rowkey = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    // 索引拿到rowkey 去元数据表中获取元数据
                    Get get = new Get(Bytes.toBytes(rowkey));
                }

            }
            table.close();
        } catch (IOException e) {
            LOGGER.error("##### Hbase获取资讯列表失败");
            e.printStackTrace();
        }
        return dataList;
    }





    /**
     * 获取Hbase 查询Reg
     * 根据 开始日期+结束日期+查询关键字 组合成正则字符串
     * @param timeStart 开始时间 格式只能是yyyyMMdd
     * @param timeEnd   结束时间 格式只能是yyyyMMdd
     * @param titleStr  标题关键词
     * @return 正则表达式
     */
    private static String getTitleRegex(String timeStart, String timeEnd, String titleStr) {
        titleStr = titleStr.replace("*", "\\*").replace("(", "\\(").replace(")", "\\)")
                .replace("}", "\\}").replace("{", "\\{").replace("|", "\\|");
        if (StringUtils.isEmpty(timeStart) && StringUtils.isEmpty(timeEnd) && StringUtils.isEmpty(titleStr)) {
            return null;
        }
        String regex = "";
        //按照开始日期和结束日期拼接正则字符串，拼接后类似这样(20181010|20181011|20181012|20181013|20181014|20181015)
        if (!StringUtils.isEmpty(timeStart) && !StringUtils.isEmpty(timeEnd)) {
            regex += "(";
            LocalDate start = LocalDate.parse(timeStart, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate end = LocalDate.parse(timeEnd, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDateTime startTime = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 0, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(end.getYear(), end.getMonth(), end.getDayOfMonth(), 0, 0, 0);
            while (startTime.isBefore(endTime) || startTime.isEqual(endTime)) {
                String ymd = startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                regex += ymd + "|";
                startTime = startTime.plusDays(1);
            }
            regex = regex.substring(0, regex.lastIndexOf("|"));
            regex += ")";
        }
        if (StringUtils.isEmpty(regex)) {
            regex += "[0-9]{4}";
        }
        if (StringUtils.isEmpty(titleStr)) {
            regex += ".*";
        } else {
            regex += ".*" + titleStr + ".*";
        }
//        return "\\S{" + (paramConfig.getIdFixNum() + 2) + "}" + regex;
        return "\\S{" + "}" + regex;
    }

    public static void main(String[] args) {
        System.out.println(getTitleRegex("20181010", "20181111", "aa"));
        String regex = "(20181010|20181011|20181012|20181013|20181014|20181015|20181016|20181017|20181018|20181019|20181020|20181021|20181022|20181023|20181024|20181025|20181026|20181027|20181028|20181029|20181030|20181031|20181101|20181102|20181103|20181104|20181105|20181106|20181107|20181108|20181109|20181110|20181111).*aa.*";
        System.out.println(Pattern.matches(regex,"20181032ccaaacc"));
    }

}