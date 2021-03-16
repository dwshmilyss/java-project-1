package com.yiban.hbase.batch;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @ToString：作用于类，覆盖默认的toString()方法，可以通过of属性限定显示某些字段，通过exclude属性排除某些字段。
 * @EqualsAndHashCode：作用于类，覆盖默认的equals和hashCode
 * @NonNull：主要作用于成员变量和参数中，标识不能为空，否则抛出空指针异常。
 * @Getter/@Setter: 作用类上，生成所有成员变量的getter/setter方法；作用于成员变量上，生成该成员变量的getter/setter方法。可以设定访问权限及是否懒加载等。
 *
 * @NoArgsConstructor, @RequiredArgsConstructor, @AllArgsConstructor：作用于类上，用于生成构造函数。有staticName、access等属性。
 * staticName属性一旦设定，将采用静态方法的方式生成实例，access属性可以限定访问权限。
 *
 * @NoArgsConstructor：生成无参构造器；
 * @RequiredArgsConstructor：生成包含final和@NonNull注解的成员变量的构造器；
 * @AllArgsConstructor：生成全参构造器
 * @EqualsAndHashCode：作用于类，覆盖默认的equals和hashCode
 * @NonNull：主要作用于成员变量和参数中，标识不能为空，否则抛出空指针异常。
 * @Data：作用于类上，是以下注解的集合：@ToString @EqualsAndHashCode @Getter @Setter @RequiredArgsConstructor
 * @Builder：作用于类上，将类转变为建造者模式
 * @Log：作用于类上，生成日志变量。针对不同的日志实现产品，有不同的注解：
 *
 */
@Data
@Slf4j
public class MyCase {
    private String c_code;
    private String c_rcode;
    private String c_region;
    private String c_cate;
    private String c_start;
    private String c_end;
    private long c_start_m;
    private long c_end_m;
    private String c_name;
    private String c_mark;


    @Override
    public String toString() {
        return c_code + ","
                + c_rcode + ","
                + c_region + ","
                + c_cate + ","
                + c_start + ","
                + c_end + ","
                + c_start_m + ","
                + c_end_m + ","
                + c_name + ","
                + c_mark + "\r\n";
    }

    public static void main(String[] args) {
        log.info("aaaaaaaaa");
    }
}
