import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * @auther WEI.DUAN
 * @date 2021/6/5
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Junit5Demo {
    @BeforeEach
    public void before() {
        System.out.println("before");
    }

    @org.junit.jupiter.api.Test
    public void test() {
        System.out.println("testing");
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