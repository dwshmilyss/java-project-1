package com.yiban.javaBase.dev.syntax;

import java.io.File;

/**
 * @auther WEI.DUAN
 * @date 2018/10/31
 * @website http://blog.csdn.net/dwshmilyss
 *
 * 该类想要直接运行 需要在JVM参数上加上 -javaagent:D:\source_code\java-project-1\java-base\target\test.jar -XX:-UseCompressedOops
 *
 */
public class ObjectSizeTest {

    /**
     * -XX:+UseCompressedOops: mark/4 + metedata/8 + 4 = 16
     * -XX:-UseCompressedOops: mark/8 + metedata/8 + 4 + padding/4 = 24
     */
    static class A {
        int a;
    }

    /**
     * -XX:+UseCompressedOops: mark/4 + metedata/8 + 4 + 4 + padding/4 = 24
     * -XX:-UseCompressedOops: mark/8 + metedata/8 + 4 + 4 = 24
     */
    static class B {
        int a;
        int b;
    }

    /**
     * -XX:+UseCompressedOops: mark/4 + metedata/8 + 4 + 4 + padding/4 = 24
     * -XX:-UseCompressedOops: mark/8 + metedata/8 + 8 + 4 + padding/4 = 32
     */
    static class B2 {
        int b2a;
        Integer b2b;
    }

    /**
     * 不考虑对象头：
     * 4 + 4 + 4 * 3 + 3 * SizeOfObject.sizeOf(B)
     */
    static class C extends A {
        int ba;
        B[] as = new B[3];

        C() {
            for (int i = 0; i < as.length; i++) {
                as[i] = new B();
            }
        }
    }

    static class D extends B {
        int da;
        Integer[] di = new Integer[3];
    }

    /**
     * 会算上A的实例字段
     */
    static class E extends A {
        int ea;
        int eb;
    }

    public static void main(String[] args) throws IllegalAccessException {
        System.out.println(new File("./target/classes").getAbsolutePath());
        System.out.println("SizeOfObject.sizeOf(new Object())=" + SizeOfObject.sizeOf(new Object()));
        System.out.println("SizeOfObject.sizeOf(new A())=" + SizeOfObject.sizeOf(new A()));
        System.out.println("SizeOfObject.sizeOf(new B())=" + SizeOfObject.sizeOf(new B()));
        System.out.println("SizeOfObject.sizeOf(new B2())=" + SizeOfObject.sizeOf(new B2()));
        System.out.println("SizeOfObject.sizeOf(new B[3])=" + SizeOfObject.sizeOf(new B[3]));
        System.out.println("SizeOfObject.sizeOf(new C())=" + SizeOfObject.sizeOf(new C()));
        System.out.println("fullSizeOfObject.sizeOf(new C())=" + SizeOfObject.fullSizeOf(new C()));
        System.out.println("SizeOfObject.sizeOf(new D())=" + SizeOfObject.sizeOf(new D()));
        System.out.println("fullSizeOfObject.sizeOf(new D())=" + SizeOfObject.fullSizeOf(new D()));
        System.out.println("SizeOfObject.sizeOf(new int[3])=" + SizeOfObject.sizeOf(new int[3]));
        System.out.println("SizeOfObject.sizeOf(new Integer(1)=" + SizeOfObject.sizeOf(new Integer(1)));
        System.out.println("SizeOfObject.sizeOf(new Integer[0])=" + SizeOfObject.sizeOf(new Integer[0]));
        System.out.println("SizeOfObject.sizeOf(new Integer[1])=" + SizeOfObject.sizeOf(new Integer[1]));
        System.out.println("SizeOfObject.sizeOf(new Integer[2])=" + SizeOfObject.sizeOf(new Integer[2]));
        System.out.println("SizeOfObject.sizeOf(new Integer[3])=" + SizeOfObject.sizeOf(new Integer[3]));
        System.out.println("SizeOfObject.sizeOf(new Integer[4])=" + SizeOfObject.sizeOf(new Integer[4]));
        System.out.println("SizeOfObject.sizeOf(new A[3])=" + SizeOfObject.sizeOf(new A[3]));
        System.out.println("SizeOfObject.sizeOf(new E())=" + SizeOfObject.sizeOf(new E()));

        /**
         * -XX:-UseCompressedOops
         SizeOfObject.sizeOf(new Object())=16
         SizeOfObject.sizeOf(new A())=24
         SizeOfObject.sizeOf(new B())=24
         SizeOfObject.sizeOf(new B2())=32
         SizeOfObject.sizeOf(new B[3])=48
         SizeOfObject.sizeOf(new C())=40
         fullSizeOfObject.sizeOf(new C())=160
         SizeOfObject.sizeOf(new D())=40
         fullSizeOfObject.sizeOf(new D())=88
         SizeOfObject.sizeOf(new int[3])=40
         SizeOfObject.sizeOf(new Integer(1)=24
         SizeOfObject.sizeOf(new Integer[0])=24
         SizeOfObject.sizeOf(new Integer[1])=32
         SizeOfObject.sizeOf(new Integer[2])=40
         SizeOfObject.sizeOf(new Integer[3])=48
         SizeOfObject.sizeOf(new Integer[4])=56
         SizeOfObject.sizeOf(new A[3])=48
         SizeOfObject.sizeOf(new E())=32
         */

        /**
         * -XX:+UseCompressedOops
         SizeOfObject.sizeOf(new Object())=16
         SizeOfObject.sizeOf(new A())=16
         SizeOfObject.sizeOf(new B())=24
         SizeOfObject.sizeOf(new B2())=24
         SizeOfObject.sizeOf(new B[3])=32
         SizeOfObject.sizeOf(new C())=24
         fullSizeOfObject.sizeOf(new C())=128
         SizeOfObject.sizeOf(new D())=32
         fullSizeOfObject.sizeOf(new D())=64
         SizeOfObject.sizeOf(new int[3])=32
         SizeOfObject.sizeOf(new Integer(1)=16
         SizeOfObject.sizeOf(new Integer[0])=16
         SizeOfObject.sizeOf(new Integer[1])=24
         SizeOfObject.sizeOf(new Integer[2])=24
         SizeOfObject.sizeOf(new Integer[3])=32
         SizeOfObject.sizeOf(new Integer[4])=32
         SizeOfObject.sizeOf(new A[3])=32
         SizeOfObject.sizeOf(new E())=24
         */
    }
}
