package com.yiban.javaBase.dev.annotations.lombok;

import lombok.*;
import lombok.extern.log4j.Log4j2;

/**
 * test lombok
 *
 * @Data注解的作用相当于 @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode的合集。
 * @Log 用的是java的日志输出（一般不建议使用）
 * @Log4j 用的是log4j的输出（或者是slf4j） 建议用这个。
 * @auther WEI.DUAN
 * @date 2017/11/30
 * @website http://blog.csdn.net/dwshmilyss
 */
@Log4j2
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Person {
    private String id;
    private String name;
    private String identity;
}
