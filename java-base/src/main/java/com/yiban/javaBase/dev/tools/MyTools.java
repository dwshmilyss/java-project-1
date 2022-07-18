package com.yiban.javaBase.dev.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import com.yiban.javaBase.dev.encrypt.MD5;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MyTools {
    final static private Logger log = LoggerFactory.getLogger(MyTools.class);

    public static final String LOGINURL =  "/doLogin";

    public static final String UPLOADFILE = "upload";
    public static final String SMALLNAME = "_small";
    public static final String SMALL2NAME = "_small2";
    public static final String MIDDLENAME = "_middle";
    public static final String BIGNAME = "_big";

    /**
     * 获取classes下的配置文件
     *
     * @param field
     * @return
     * @author junqing.cao
     * @version 2012-4-23
     */
    public static List<String> getAllPropertyVal(String field) {
        String returnVal = "";
        InputStream in = null;
        Properties properties = null;
        List<String> models = null;
        try {
            in = MyTools.class.getClassLoader().getResourceAsStream(field);

            if (in == null) {
                log.error("[" + field + "] --> not exist");
            } else {
                properties = new Properties();
                properties.load(in);
                if (properties != null) {
                    models = new ArrayList<String>();
                    for (Iterator iterator = properties.keySet().iterator(); iterator
                            .hasNext();) {
                        String key = (String) iterator.next();
                        models.add(properties.getProperty(key));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally { // 关闭流
            try {
                if (properties != null) {
                    properties.clear();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return models;
    }

    /**
     * 获取classes下的配置文件
     *
     * @param field
     * @param key
     * @return
     * @author junqing.cao
     * @version 2012-4-23
     */
    public static String getPropertyVal(String field, String key) {
        String returnVal = "";
        InputStream in = null;
        Properties properties = null;

        try {
            in = MyTools.class.getClassLoader().getResourceAsStream(field);

            if (in == null) {
                log.error("[" + field + "] --> not exist");
            } else {
                properties = new Properties();
                properties.load(in);

                returnVal = properties.getProperty(key);
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally { // 关闭流
            try {
                if (properties != null) {
                    properties.clear();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return returnVal;
    }

    /**
     * 检测字符串是否不为空(null,"","null")
     *
     * @param s
     * @return 不为空则返回true，否则返回false
     */
    public static boolean notEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s);
    }

    /**
     * 检测字符串是否为空(null,"","null")
     *
     * @param s
     * @return 为空则返回true，不否则返回false
     */
    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || "null".equals(s);
    }

    /**
     * 字符串转换为字符串数组
     *
     * @param str
     *            字符串
     * @param splitRegex
     *            分隔符
     * @return
     */
    public static String[] str2StrArray(String str, String splitRegex) {
        if (isEmpty(str)) {
            return null;
        }
        return str.split(splitRegex);
    }

    /**
     * 用默认的分隔符(,)将字符串转换为字符串数组
     *
     * @param str
     *            字符串
     * @return
     */
    public static String[] str2StrArray(String str) {
        return str2StrArray(str, ",\\s*");
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String date2Str(Date date) {
        return date2Str(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
     *
     * @param date
     * @return
     */
    public static Date str2Date(String date) {
        if (notEmpty(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return sdf.parse(date);
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
            return new Date();
        } else {
            return null;
        }
    }

    /**
     * 按照参数format的格式，日期转字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date, String format) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } else {
            return "";
        }
    }

    /**
     * String转Integer
     *
     * @param str
     * @return
     */
    public static Integer str2int(String str) {
        Integer num = 0;
        if (str != null && !"".equals(str)) {
            try {
                num = Integer.parseInt(str);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return num;
    }

    /**
     * 获取当前时间
     *
     * @param pattern
     *            - 时间格式化
     * @return
     */
    public static String getNowDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 获取当前日期 星期几 例：年-月-日 星期几
     *
     * @return
     */
    public static String getNowDateWeek() {
        return MyTools.getNowDate(DateParams.DATE_WEEK);
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static String getTimes() {
        return "" + Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 验证邮件地址格式是否正确
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        Pattern pattern = Pattern
                .compile("^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 格式化用户点评的时间
     */
    public static String formateTime(String dptime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = format.parse(dptime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d == null) {
            return "";
        }

        long review = d.getTime() / 1000;
        long now = System.currentTimeMillis() / 1000;
        long remain = now - review;
        long xDay = remain / (24 * 60 * 60);
        remain = remain % (24 * 60 * 60);

        long xHour = remain / (60 * 60);
        remain = remain % (60 * 60);

        long xMin = remain / (60);
        remain = remain % (60);

        long xSec = remain;
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        boolean isSameDay = false;
        if (sdf.format(d).equals(sdf.format(new Date()))) {
            isSameDay = true;
        }
        if (xDay < 1 && xHour < 2 && isSameDay) { // 当天2小时以内,显示多久前
            if (xHour > 0) {
                sb.append("" + xHour + "小时");
            } else if (xMin > 0) {
                sb.append("" + xMin + "分钟");
            } else if (xSec > 0) {
                sb.append("" + xSec + "秒");
            } else if (xSec <= 0) {
                sb.append("0秒");
            }
            sb.append(" 前");
        } else if (xDay <= 1 && isSameDay) { // 当天一天内
            sb.append("今天 " + sdf2.format(d));
        } else {
            sb.append(sdf1.format(d));
        }
        return sb.toString();
    }

    /**
     * 生成Token
     */
    public static String genRandomToken(int pwd_len) {
        // 35是因为数组是从0开始的，26个字母+10个数字
        final int maxNum = 36;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9' };

        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

    /** 随机生成激活码 */
    public static String createActivecode() {
        String activationCode = (int) (Math.random() * 9) + 1 + "";
        for (int i = 0; i < 7; i++) {
            activationCode += (int) (Math.random() * 10);
        }
        return activationCode;
    }

    /** 生成手机验证码 */
    public static String createMobilecode() {
        String activationCode = (int) (Math.random() * 9) + 1 + "";
        for (int i = 0; i < 5; i++) {
            activationCode += (int) (Math.random() * 10);
        }
        return activationCode;
    }

    /** 生成新的指付通账号 */
    public static String createNewUserCode(String bankAreaCode,
            String bankCode, String seqCode) {
        String userCode = bankAreaCode + bankCode + seqCode;
        String b = luhnMethod(userCode);
        return userCode + b;
    }

    public static String luhnMethod(String num) {
        int i, tempInt = 0;
        String tempStr = null;
        for (i = num.length(); i > 0;) {
            int temp = Integer.parseInt(num.substring(i - 1, i)) * 2;
            i--;
            tempInt += temp / 10 + temp % 10;
            if (i == 0) {
                break;
            }
            tempInt += Integer.parseInt(num.substring(i - 1, i));
            i--;
        }
        if (tempInt % 10 == 0) {
            tempStr = "0";
        } else {
            tempStr = String.valueOf(10 - tempInt % 10);
        }
        return tempStr;
    }

    /**
     * 判断字符串是否是数字
     *
     * @param str
     * @return:true-是;false-否
     */
    public static boolean isNumber(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }


    public static String convertStreamToString(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 获取去除"-"的UUID
     *
     * @return
     *
     * @auther junqing.cao
     * @date 2013-9-26
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13)
                + uuid.substring(14, 18) + uuid.substring(19, 23)
                + uuid.substring(24);
    }

    /**
     * 从flashMap中获取数据
     *
     * @param request
     * @param key
     * @return
     *
     * @auther junqing.cao
     * @date 2014-1-14
     */
    public static Object getFlashMapValue(HttpServletRequest request, String key) {
        return RequestContextUtils.getInputFlashMap(request).get(key);
    }

    /**
     * 显示银行账号时没4个数字空一格
     *
     * @param accountCode
     * @return
     *
     * @auther yalan.huang
     * @date 2014-1-22
     */
    public static String getAccountCodeValue(String accountCode) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < accountCode.length(); i++) {
            sb.append(accountCode.charAt(i));
            if ((i + 1) % 4 == 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String getSuffixByFile(File file) throws IOException{
        String type = getImageFormatName(file);
        log.info("image type = "+type);
        String suffix = "";
        if ("gif".equalsIgnoreCase(type)) {
            suffix = "gif";
        }else if ("jpeg".equalsIgnoreCase(type)) {
            suffix = "jpg";
        }else if ("png".equalsIgnoreCase(type)) {
            suffix = "png";
        }else if ("bmp".equalsIgnoreCase(type)) {
            suffix = "bmp";
        }
        return suffix;
    }
    
    
    public static String getImageFormatName(File file) throws IOException {
        String formatName = null;

        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> imageReader = ImageIO.getImageReaders(iis);
        if (imageReader.hasNext()) {
            ImageReader reader = imageReader.next();
            formatName = reader.getFormatName();
        }

        return formatName;
    }
    
    /**
     * 获取当前日期的前一天
     *
     *
     * @auther yalan.huang
     * @date 2014-1-22
     */
    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = date2Str(calendar.getTime(), "yyyyMMdd");
        return date;
    }
    
    
    /**
     * 获取某个文件的前缀路径
     *
     * 不包含文件名的路径
     *
     * @param path
     *            当前文件路径
     * @return 不包含文件名的路径
     * @throws Exception
     */
    public static String getFilePrefixPath(String path) throws Exception {
        if (null == path || path.isEmpty()) {
            throw new Exception("文件路径为空！");
        }
        int index = path.lastIndexOf(File.separator);
        if (index > 0) {
            path = path.substring(0, index + 1);
        }
        return path;
    }

    /**
     * 获取不包含后缀的文件路径
     *
     * @param src
     * @return
     */
    public static String getPathWithoutSuffix(String src) {
        String path = src;
        int index = path.lastIndexOf(".");
        if (index > 0) {
            path = path.substring(0, index + 1);
        }
        return path;
    }

    /**
     * 获取文件名
     *
     * @param filePath
     *            文件路径
     * @return 文件名
     * @throws IOException
     */
    public static String getFileName(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("not found the file !");
        }
        return file.getName();
    }
    
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("\\([0-9]{1}\\)");
        return pattern.matcher(str).matches();
     }
    
    /**
     * 文件重命名 (规则：文件名后面拼接(num++))
     * @param path 源文件路径
     * @return 重命名后的路径
     * @throws Exception
     */
    public static synchronized String renameFileAuto(String path) throws Exception {
        String fileName = getFileName(path);
//        StringTokenizer st = new StringTokenizer(fileName, ".");
//        String name = st.nextToken();
//        String suffix = st.nextToken();
        
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        
        String prefixPath = getFilePrefixPath(path);
        String newPath = "";
        String newName = "";
        if (name.contains("(")) {
            String temp = name.substring(name.lastIndexOf("("));
           // log.info("name = "+name+" suffix = "+suffix+" prefixPath = "+prefixPath+" temp = "+temp);
            if (name.length() >3 && isNumeric(temp)) {
                String num = temp.substring(1,2);
                log.info("num = "+num);
                int count = Integer.parseInt(num)+1;
                newName = name.substring(0,name.lastIndexOf("(")+1) + count+")";
            }else{
                newName = name + "(1)";
            }
        }else{
            newName = name + "(1)";
        }
        newPath = prefixPath+newName+"."+suffix;
        log.info("newName = "+newName+" newPath = "+newPath);
        return newPath;
    }
    
    /**
     * 等比压缩 按原图的宽高进行计算
     * @param srcW
     * @param srcH
     * @param comW
     * @param comH
     * @return 按比例返回宽和高
     */
    public static Map<String,Integer> getZoomHW(float srcW,float srcH,float comW,float comH){
        int realH = 0;
        int realW = 0;
        Map<String,Integer> map = new HashMap<String, Integer>();
        float tempW = srcW / comW;
        BigDecimal bW = new BigDecimal(tempW);
        float rateW = bW.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        
        
        float tempH = srcH / comH;
        BigDecimal bH = new BigDecimal(tempH);
        float rateH = bH.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        
        if (rateW > rateH) {//按比例大的一边进行压缩
            realW= (int) (srcW/rateW);
            realH = (int)(srcH/rateW);
        }else if (rateW == rateH) {//如果相等就随便取一边即可
             realW= (int) (srcW/rateW);
             realH = (int)(srcH/rateW);
        }else if (rateW < rateH) {
            realW= (int) (srcW/rateH);
            realH = (int)(srcH/rateH);
       }
        map.put("w", realW);
        map.put("h", realH);
        return map;
    }
    
    public static RowBounds getRowBounds(int pageNo){
        RowBounds rowBounds;
        //如果是0就是不分页
        if (pageNo == 0){
            rowBounds = new RowBounds();
        }else{
            rowBounds = new RowBounds(pageNo, ParameterTools.page_limit);
        }
        return rowBounds;
    }
    
    /**
     * 校验接口的调用是否合法
     *  MD5.encrypt(sign+timestamp) 和 客户端传入的加密字符串比对，一致就是合法 否则不合法
     * @author wei.duan
     * @date 2017-6-12 上午11:40:54
     * @Description: TODO
     * @param encrypt 客户端传入的md5加密字符串
     * @param timestamp 时间戳
     * @return
     */
    public static boolean checkAPI(String encrypt,String timestamp){
        String sign = MyTools.getPropertyVal("props/parameters.properties", "encrypt");
        String res = MD5.encrypt(sign+timestamp);
        if (encrypt != null && encrypt.equalsIgnoreCase(res)) {
            return true;
        }else {
            return false;
        }
    }
    
    public static void main(String[] args) {
        String fileName = "1.1.jpg";
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        System.out.println("name = "+name + " suffix = "+suffix);
    }
    
}
