package com.linkflow.analysis.presto.security;

import com.linkflow.analysis.presto.security.util.Config;
import com.linkflow.analysis.presto.security.util.ConfigLoader;
import com.linkflow.analysis.presto.security.util.SugarSecurityUtil;
import com.facebook.airlift.log.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SecurityUtil {
    private final Logger logger = Logger.get(SecurityUtil.class);

    // user -> password
    public final Map<String, String> userInfo = new ConcurrentHashMap<>();
    // user -> schemas
    public final Map<String, Set<String>> accessSchemaInfo = new ConcurrentHashMap<>();
    // user -> tables
    public final Map<String, Set<String>> accessTableInfo = new ConcurrentHashMap<>();

    private static class SecurityUtilHolder {
        private static final SecurityUtil INSTANCE = new SecurityUtil();
    }

    private SecurityUtil() {
        initConfig();
        this.logger.info("Security schedule start");
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        try {
            long step = Long.valueOf(Config.configs.get("presto.access-control-interval").toString());
            logger.warn("step = "  + step);
            long begin = 0L;
            service.scheduleAtFixedRate(new ImportConfig(), begin, step, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("func SecurityUtil error :" + e);
        }
    }

    public static final SecurityUtil getInstance() {
        return SecurityUtilHolder.INSTANCE;
    }

    class ImportConfig implements Runnable {
        public void run() {
            SecurityUtil.this.initConfig();
        }
    }

    public void initConfig() {
        //读取配置文件
        try {
            Config.init();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("func config error :" + e);
        }
        //1.先处理配置文件 presto-auth.properties
        Set<String> tmpInvalidUser = new HashSet<>();
        tmpInvalidUser.addAll(this.userInfo.keySet());//先把上一次的 userInfo 添加到tmpInvalidUser
        tmpInvalidUser.removeAll(Config.authKeySet);//在tmpInvalidUser中移除这一次 presto-auth.properties 中存在的user
        if (tmpInvalidUser.size() > 0)//如果根据这一次的配置文件移除完之后集合中还有上一次遗留的，也移走，这就是userInfo要完全按照这一次的 presto-auth.properties 来
            for (String user : tmpInvalidUser)
                this.userInfo.remove(user);
        Map<String, String> tmpUserInfo = new HashMap<>();
        for (String key : Config.authKeySet) {
            if (Config.ISENCRYPT) {//如果开启了加密
                //这里就把原始密码加密
                String tmpPassWD = SugarSecurityUtil.encryptDes(Config.authPP.getProperty(key));
                tmpUserInfo.put(key, tmpPassWD);
                continue;
            }
            //如果不是加密的，直接放入map
            tmpUserInfo.put(key, Config.authPP.getProperty(key));
        }
        this.userInfo.putAll(tmpUserInfo);//重新把这一次的 presto-auth.properties 配置加载到userInfo中，下面 accessSchemaInfo 和 accessTableInfo 的逻辑也一样
        //2. 再处理 presto-access-schemas.properties
        tmpInvalidUser.clear();
        tmpInvalidUser.addAll(this.accessSchemaInfo.keySet());
        tmpInvalidUser.removeAll(Config.accessSchemaKeySet);
        if (tmpInvalidUser.size() > 0)
            for (String user : tmpInvalidUser)
                this.accessSchemaInfo.remove(user);
        Map<String, Set<String>> tmpAccessSchemaInfo = new HashMap<>();
        for (String key : Config.accessSchemaKeySet) {
            String schemaValue = Config.accessSchemaPP.getProperty(key);
            List<String> schemaValueList = Arrays.asList(schemaValue.split(","));
            Set<String> schemaSet = new HashSet<>(schemaValueList);
            schemaSet.add("information_schema");//默认就加上 information_schema
            tmpAccessSchemaInfo.put(key, schemaSet);
        }
        this.accessSchemaInfo.putAll(tmpAccessSchemaInfo);
        //TODO 3. 最后处理 user -> tables
        tmpInvalidUser.clear();
        tmpInvalidUser.addAll(this.accessTableInfo.keySet());
        tmpInvalidUser.removeAll(Config.accessTableKeySet);
        if (tmpInvalidUser.size() > 0)
            for (String user : tmpInvalidUser)
                this.accessTableInfo.remove(user);
        Map<String, Set<String>> tmpAccessTableInfo = new HashMap<>();
        for (String key : Config.accessTableKeySet) {
            String tableValue = Config.accessTablePP.getProperty(key);
            List<String> tableValueList = Arrays.asList(tableValue.split(","));
            Set<String> tableSet = new HashSet<>(tableValueList);
            //columns,tables,views,table_privileges,schemata,roles,enabled_roles,applicable_roles
            tableSet.addAll(Arrays.asList("columns"));
            tmpAccessTableInfo.put(key, tableSet);
        }
        this.accessTableInfo.putAll(tmpAccessTableInfo);
    }

    public void test() {
        ImportConfig importConfig = new ImportConfig();
        importConfig.run();
    }

    public static void main(String[] args) {
        SecurityUtil securityUtil = new SecurityUtil();
        securityUtil.test();
    }
}
