package com.yiban.javaBase.dev.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterTools {
    private static final Logger log = LoggerFactory.getLogger(ParameterTools.class);

    /**
     * 属性配置文件路径
     */
    private static final String PARAMETERS_PROPERTIES_PATH = "props/parameters.properties";

    /** Mapper文件包名 */
    public static final String MAPPER_PATH = "com.yiban.manage.data";

    /** 邮件模板所在文件夹 */
    public static final String MAIL_TEMPLATES_PATH = "mailTemplates";
    /** 翻页每页显示数 */
    public static Integer page_limit;
    /** 网站域名 */
    public static String WEBROOT;
    /**
     * 读取参数
     */
    static {
        InputStream fis = null;
        PropertyResourceBundle props = null;

        try {
            fis = ParameterTools.class.getClassLoader().getResourceAsStream(
                    PARAMETERS_PROPERTIES_PATH);
            props = new PropertyResourceBundle(fis);
            // 分页每页显示数
            page_limit = Integer.parseInt(props.getString("PAGE_LIMIT"));
            WEBROOT = props.getString("WEBROOT");
        } catch (IOException e) {
            log.error("读取配置文件" + PARAMETERS_PROPERTIES_PATH + "错误", e);
        } finally {
            props = null;
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error("配置文件" + PARAMETERS_PROPERTIES_PATH + "读取流关闭错误", e);
            }
        }
    }
}
