package com.yiban.javaBase.dev.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * the demo of exception
 *
 * @auther WEI.DUAN
 * @date 2017/8/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ExceptionDemo {

    private static final Logger log = LoggerFactory.getLogger(ExceptionDemo.class);

    /**
     * 在finally中关闭资源
     */
    public void closeResourceInFinally() {
        FileInputStream inputStream = null;
        try {
            File file = new File("./tmp.txt");
            inputStream = new FileInputStream(file);
            // use the inputStream to read a file
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 从JAVA7开始  实现了AutoCloseable接口的类可以使用Try-With-Resource语句
     * 它将在try被执行后自动关闭，或者处理一个异常。
     */
    public void automaticallyCloseResource() {
        File file = new File("./tmp.txt");
        try (FileInputStream inputStream = new FileInputStream(file);) {
            // use the inputStream to read a file
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}



