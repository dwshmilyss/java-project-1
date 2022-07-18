package com.linkflow.analysis.presto.security.util;

import com.facebook.airlift.log.Logger;
import io.minio.MinioClient;
import io.minio.ObjectStat;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Config {
    private static final Logger logger = Logger.get(Config.class);

    //user -> password
    public static Properties authPP = null;

    //user -> schemas
    public static Properties accessSchemaPP = null;

    //user -> tables
    public static Properties accessTablePP = null;

    public static Properties passwordPP = null;

    public static Set<String> authKeySet = null;

    public static Set<String> accessSchemaKeySet = null;

    public static Set<String> accessTableKeySet = null;
    public static Map<String, Object> configs;

    public static boolean ISENCRYPT;
    public static MinioClient minioClient;
    public static String minioUrl;
    public static String minioKey;
    public static String minioSecret;
    public static String minioBucket;

    static {
        //init oss
        configs = ConfigLoader.getConfig();
//        minioUrl = configs.get("oss.minio.url").toString();
//        minioKey = configs.get("oss.minio.key").toString();
//        minioSecret = configs.get("oss.minio.secret").toString();
//        minioBucket = configs.get("oss.bucket").toString();
//        minioClient = MinioClient.builder().endpoint(minioUrl).credentials(minioKey, minioSecret).build();

        // load password config
//        ossWithPasswordAuth();
        //        localWithPasswordAuth();
        redisWithPasswordAuth();
    }

    public static void init() throws Exception {
//        localWithSystemAccessControl();
//        ossWithSystemAccessControl();
        redisWithSystemAccessControl();
    }

    public static void localWithPasswordAuth() {
        FileReader passwordReader = null;
        try {
            String passwordPath = "/etc/presto/password-authenticator.properties";
            passwordPP = new Properties();
            passwordReader = new FileReader(passwordPath);
            passwordPP.load(passwordReader);
            ISENCRYPT = Boolean.valueOf(passwordPP.getProperty("password.encryption.enable", "false")).booleanValue();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("config password-authenticator.properties init error :" + e);
        } finally {
            try {
                if (passwordReader != null)
                    passwordReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("when password-authenticator.properties initial and close stream error,message : ", e);
            }
        }
    }

    public static void localWithSystemAccessControl() throws IOException {
        String userInfoPath = "/etc/presto/presto-auth.properties";
        String accessSchemaInfoPath = "/etc/presto/presto-access-schemas.properties";
        String accessTableInfoPath = "/etc/presto/presto-access-tables.properties";
        FileReader authReader = null;
        FileReader accessSchemaReader = null;
        FileReader accessTableReader = null;
        authKeySet = new HashSet<>();
        accessSchemaKeySet = new HashSet<>();
        accessTableKeySet = new HashSet<>();
        authPP = new Properties();
        accessSchemaPP = new Properties();
        accessTablePP = new Properties();
        try {
            //读取presto-auth.properties信息
            authReader = new FileReader(userInfoPath);
            authPP.load(authReader);
            authKeySet = authPP.stringPropertyNames();
            //读取presto-access-schemas.properties
            accessSchemaReader = new FileReader(accessSchemaInfoPath);
            accessSchemaPP.load(accessSchemaReader);
            accessSchemaKeySet = accessSchemaPP.stringPropertyNames();
            //读取presto-access-tables.properties
            accessTableReader = new FileReader(accessTableInfoPath);
            accessTablePP.load(accessTableReader);
            accessTableKeySet = accessTablePP.stringPropertyNames();
        } finally {
            try {
                if (authReader != null)
                    authReader.close();
                if (accessSchemaReader != null)
                    accessSchemaReader.close();
                if (accessTableReader != null)
                    accessTableReader.close();
            } catch (IOException e) {
                throw new RuntimeException("when config initial and close stream error,message : ", e);
            }
        }
    }

    //oss
    public static void ossWithPasswordAuth() {
        InputStream stream = null;
        try {
            minioClient.statObject(minioBucket, "data/presto-security/presto-auth.properties");
            stream = minioClient.getObject(minioBucket, "data/presto-security/presto-auth.properties");
            passwordPP = new Properties();
            passwordPP.load(stream);
            ISENCRYPT = Boolean.valueOf(passwordPP.getProperty("password.encryption.enable", "false")).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("config password-authenticator.properties init error in oss :" + e);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("when password-authenticator.properties initial and close stream error,message : ", e);
            }
        }
    }

    //oss
    public static void ossWithSystemAccessControl() throws Exception {
        InputStream authInputStream = null;
        InputStream accessSchemaInputStream = null;
        InputStream accessTableInputStream = null;
        String userInfoPath = "data/presto-security/presto-auth.properties";
        String accessSchemaInfoPath = "data/presto-security/presto-access-schemas.properties";
        String accessTableInfoPath = "data/presto-security/presto-access-tables.properties";
        authKeySet = new HashSet<>();
        accessSchemaKeySet = new HashSet<>();
        accessTableKeySet = new HashSet<>();
        authPP = new Properties();
        accessSchemaPP = new Properties();
        accessTablePP = new Properties();
        try {
            //读取presto-auth.properties信息
            minioClient.statObject(minioBucket, userInfoPath);
            authInputStream = minioClient.getObject(minioBucket, userInfoPath);
            authPP.load(authInputStream);
            authKeySet = authPP.stringPropertyNames();
            //读取presto-access-schemas.properties
            minioClient.statObject(minioBucket, accessSchemaInfoPath);
            accessSchemaInputStream = minioClient.getObject(minioBucket, accessSchemaInfoPath);
            accessSchemaPP.load(accessSchemaInputStream);
            accessSchemaKeySet = accessSchemaPP.stringPropertyNames();
            //读取presto-access-tables.properties
            minioClient.statObject(minioBucket, accessTableInfoPath);
            accessTableInputStream = minioClient.getObject(minioBucket, accessTableInfoPath);
            accessTablePP.load(accessTableInputStream);
            accessTableKeySet = accessTablePP.stringPropertyNames();
        } finally {
            try {
                if (authInputStream != null)
                    authInputStream.close();
                if (accessSchemaInputStream != null)
                    accessSchemaInputStream.close();
                if (accessTableInputStream != null)
                    accessTableInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("when config initial and close stream error,message : ", e);
            }
        }
    }

    //redis
    public static void redisWithPasswordAuth() {
        try {
            passwordPP = new Properties();
            passwordPP.setProperty("password.encryption.enable", JRedisUtil.get("password.encryption.enable"));
            ISENCRYPT = Boolean.valueOf(passwordPP.getProperty("password.encryption.enable", "false")).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("config password-authenticator.properties init error in oss :" + e);
        }
    }

    //redis
    public static void redisWithSystemAccessControl() throws Exception {
        authKeySet = new HashSet<>();
        accessSchemaKeySet = new HashSet<>();
        accessTableKeySet = new HashSet<>();
        authPP = new Properties();
        accessSchemaPP = new Properties();
        accessTablePP = new Properties();
        //读取presto-auth.properties信息
        Map<String, String> authMap = JRedisUtil.hgetall("presto_auth_user");
        authPP.putAll(authMap);
        authKeySet = authPP.stringPropertyNames();
        //读取presto-access-schemas.properties
        Map<String, String> accessSchemaMap = JRedisUtil.hgetall("presto_access_schemas");
        accessSchemaPP.putAll(accessSchemaMap);
        accessSchemaKeySet = accessSchemaPP.stringPropertyNames();
        //读取presto-access-tables.properties
        Map<String, String> accessTableMap = JRedisUtil.hgetall("presto_access_tables");
        accessTablePP.putAll(accessTableMap);
        accessTableKeySet = accessTablePP.stringPropertyNames();
    }

    public static void main(String[] args) {
        try {
            init();
            System.out.println(ISENCRYPT);
            System.out.println(authKeySet.size());
            for (String string : authKeySet) {
                System.out.println("authKeySet.key = " + string);
            }
            System.out.println(accessSchemaKeySet.size());
            for (String string : accessSchemaKeySet) {
                System.out.println("accessSchemaKeySet.key = " + string);
            }
            System.out.println(accessTableKeySet.size());
            for (String string : accessTableKeySet) {
                System.out.println("accessTableKeySet.key = " + string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
