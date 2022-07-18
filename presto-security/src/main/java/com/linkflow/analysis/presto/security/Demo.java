package com.linkflow.analysis.presto.security;

import com.linkflow.analysis.presto.security.util.Config;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Demo {
    public static Properties passwordPP = null;
    public static boolean ISENCRYPT;

    static {
        FileReader passwordReader = null;
        try {
            String passwordPath = "/Users/edz/Desktop/password-authenticator.properties";
            passwordPP = new Properties();
            passwordReader = new FileReader(passwordPath);
            passwordPP.load(passwordReader);
            ISENCRYPT = Boolean.valueOf(passwordPP.getProperty("password.encryption.enable", "false")).booleanValue();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (passwordReader != null)
                    passwordReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
//        SecurityUtil.getInstance();
//        System.out.println("ISENCRYPT = " + ISENCRYPT);
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList("a", "b", "c"));
        System.out.println(set.toString());
    }
}
