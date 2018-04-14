package com.yiban.javaBase.dev.syntax;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * 枚举
 * 枚举是Java1.5出来之后新增的类型，它可以用来定义一组取值范围固定的的变量。
 * compareTo(E o) : 比较枚举元素的顺序
 * equals(Object obj) : 判断枚举元素是否相同
 * name() :  获取元素定义时的名称
 * ordinal() : 获取枚举元素被定义时的顺序，从0开始计算。枚举值默认为从0开始的有序数值
 * values()：返回enum实例的数组，而且该数组中的元素严格保持在enum中声明时的顺序。
 * java.lang.Enum实现了Comparable和 Serializable 接口，所以也提供 compareTo() 方法。
 *
 * @auther WEI.DUAN
 * @create 2017/5/1
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class EnumDemo {

    public static void main(String[] args) {
//        testSwitch(ColorValues.BLUE);
//        getEnumSet();
//        printColor();
        printColorValues();
    }

    /**
     * 调用无参枚举
     */
    public static void printColor() {
        for (Color color : Color.values()) {
            System.out.println("第" + (color.ordinal() + 1) + "个枚举是：" + color.name());
            System.out.println("此枚举和枚举BLUE比较的值为：" + color.compareTo(color.BLUE));
            System.out.println("用'equals()方法'判断此枚举是否和枚举BLUE相等为：" + color.equals(color.BLUE));
            System.out.println("用'== '的形式判断此枚举是否和枚举BLUE相等为：" + (color == color.BLUE));
            System.out.println(color.getDeclaringClass());
            System.out.println("========================================================");
        }
    }

    /**
     * 调用有参枚举
     */
    public static void printColorValues() {
        for (ColorValues colorValues : ColorValues.values()) {
            System.out.println("第" + (colorValues.ordinal() + 1) + "个枚举是：" + colorValues.name());
            System.out.println("此枚举的值为：" + colorValues.getDescription());
            System.out.println("此枚举和枚举BLUE比较的值为：" + colorValues.compareTo(colorValues.BLUE));
            System.out.println("用'equals()方法'判断此枚举是否和枚举BLUE相等为：" + colorValues.equals(colorValues.BLUE));
            System.out.println("用'== '的形式判断此枚举是否和枚举BLUE相等为：" + (colorValues == colorValues.BLUE));
            System.out.println(colorValues.getDeclaringClass());
            System.out.println("========================================================");

        }
    }

    /**
     * 枚举用于switch
     * 事实上是因为枚举类会自动的为它的每一个元素生成一个整数的顺序编号(可以通过ordinal()方法获知其编号),
     * 所以当我们向switch传入枚举类型时，事实上，传进去的是一个整数--元素的顺序编号。
     */
    public static void testSwitch(ColorValues colorValues) {
        switch (colorValues) {
            case RED:
                System.out.println("this is " + colorValues.getDescription());
                break;
            case BLUE:
                System.out.println("this is " + colorValues.getDescription());
                break;
            case BRACK:
                System.out.println("this is " + colorValues.getDescription());
                break;
            case GREEN:
                System.out.println("this is " + colorValues.getDescription());
                break;
            case WHITE:
                System.out.println("this is " + colorValues.getDescription());
                break;
            case PURPLE:
                System.out.println("this is " + colorValues.getDescription());
                break;
            case YELLOW:
                System.out.println("this is " + colorValues.getDescription());
                break;
            default:
                System.out.println("null");
                break;
        }
    }

    /**
     * 获取元素信息并打印输出
     *
     * @param enumSet
     */
    public static void getElement(EnumSet<ColorValues> enumSet) {
        StringBuffer strName = new StringBuffer();
        for (int j = 0; j < enumSet.size(); j++) {
            System.out.print("\t" + enumSet.toArray()[j]);
            for (ColorValues colorValues2 : enumSet) {
                strName.append("\t" + colorValues2.getDescription() + "\n");
            }
            System.out.println(strName.toString().split("\n")[j]);
        }
    }

    /**
     * EnumSet方法的具体使用实例
     * 1.枚举集合，和Set集合一样，保证每一个元素的唯一性，不可重复性；
     * 2.当创建EnumSet对象时，需要显式或隐式指明元素的枚举类型；
     * 3.此对象中的元素仅能取自同一枚举类
     * 4.在EnumSet内部以"位向量"的形式表示,这种结构紧凑而高效，使得类的时间、控件性能非常优越。
     * <p/>
     * allOf(Class<E> elementType): 创建一个EnumSet，它包含了elementType 中所有枚举元素
     * complementOf(EnumSet<E> s):    创建一个EnumSet，其中的元素是s的补集
     * noneOf(Class<E> elementType): 创建一个EnumSet，其中的元素的类型是elementType，但是没有元素
     * range(E from, E to): 创建一个EnumSet，其中的元素在from和to之间，包括端点
     * add(E e): 增加一个元素e
     * remove(Object o): 删除一个元素o
     * addAll(Collection<? extends E>): 增加一个集合元素c
     * removeAll(Collection<?> c):    删除一集合元素c
     * 不能在EnumSet中增加null元素，否则将会抛出空指针异常
     */
    public static void getEnumSet() {
        EnumSet<ColorValues> enumSet1 = EnumSet.allOf(ColorValues.class);
        System.out.println("enumSet1枚举集合中元素有" + enumSet1.size() + "个");
        System.out.println("分别是：");
        getElement(enumSet1);

        System.out.println("=====================添加元素============================");
        EnumSet<ColorValues> enumSet2 = EnumSet.noneOf(ColorValues.class);
        System.out.println("\n此时枚举集合enumSet2中元素有" + enumSet2.size() + "个");

        System.out.println("\n向枚举集合enumSet2添加一个元素" + ColorValues.BLUE);
        enumSet2.add(ColorValues.BLUE);
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("\n向枚举集合enumSet2添加一个元素" + ColorValues.RED);
        enumSet2.add(ColorValues.RED);
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("\n向枚举集合enumSet2添加一个元素" + ColorValues.GREEN);
        enumSet2.add(ColorValues.GREEN);
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("\n向枚举集合enumSet2添加ColorValues的补集enumSet3");
        EnumSet<ColorValues> enumSet3 = EnumSet.complementOf(enumSet2);
        getElement(enumSet3);

        enumSet2.addAll(enumSet3);
        System.out.println("\n此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("=====================删除元素============================");
        System.out.println("\n枚举集合enumSet2删除一个元素" + ColorValues.GREEN);
        enumSet2.remove(ColorValues.GREEN);
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("\n枚举集合enumSet2删除一个固定范围内的元素");
        enumSet2.removeAll(enumSet2.range(ColorValues.YELLOW, ColorValues.WHITE));
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("\n枚举集合enumSet2删除一个枚举集合enumSet3，这个集合的元素个数有：" + enumSet3.size());
        enumSet2.removeAll(enumSet3);
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());
        getElement(enumSet2);

        System.out.println("=====================清除所有元素============================");
        enumSet2.clear();
        System.out.println("此时枚举集合enumSet2中元素个数有：" + enumSet2.size());

    }


    /**
     * EnumMap的具体运用
     */
    public static void getEnumMap() {
        EnumMap<ColorValues, String> enumMap1 = new EnumMap<ColorValues, String>(
                ColorValues.class);
        String[] Weeks = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};
        System.out.println("==========================添加枚举键值对================================");
        enumMap1.put(ColorValues.BLUE, ColorValues.BLUE.getDescription() + Weeks[ColorValues.BLUE.ordinal()]);
        System.out.println("key=0时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        enumMap1.put(ColorValues.BRACK, ColorValues.BRACK.getDescription() + Weeks[ColorValues.BRACK.ordinal()]);
        System.out.println("key=1时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        enumMap1.put(ColorValues.GREEN, ColorValues.GREEN.getDescription() + Weeks[ColorValues.GREEN.ordinal()]);
        System.out.println("key=2时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        enumMap1.put(ColorValues.PURPLE, ColorValues.PURPLE.getDescription() + Weeks[ColorValues.PURPLE.ordinal()]);
        System.out.println("key=3时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        enumMap1.put(ColorValues.RED, ColorValues.RED.getDescription() + Weeks[ColorValues.RED.ordinal()]);
        System.out.println("key=4时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        enumMap1.put(ColorValues.WHITE, ColorValues.WHITE.getDescription() + Weeks[ColorValues.WHITE.ordinal()]);
        System.out.println("key=5时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        enumMap1.put(ColorValues.YELLOW, ColorValues.YELLOW.getDescription() + Weeks[ColorValues.YELLOW.ordinal()]);
        System.out.println("key=6时enumMap1的元素有 " + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        System.out.println("==========================判断枚举键值对================================");
        System.out.println("判断是否存在键" + ColorValues.BLUE);
        System.out.println("结果为：" + enumMap1.containsKey(ColorValues.BLUE));

        System.out.println("判断是否存在值'蓝色星期二'");
        System.out.println("结果为：" + enumMap1.containsValue("蓝色星期二"));

        System.out.println("==========================删除枚举键值对================================");
        enumMap1.remove(ColorValues.BLUE);
        System.out.println("剩余元素有：" + enumMap1);
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");

        System.out.println("==========================获取键对应的值================================");
        System.out.println(" 获取RED对应的值，结果为: " + enumMap1.get(ColorValues.RED));

        System.out.println("==========================清空枚举键值对================================");
        enumMap1.clear();
        System.out.println(" 此时键值对个数为: " + enumMap1.size() + "\n");


    }

    public enum Color {
        BLUE, YELLOW, RED, GREEN, WHITE, BRACK, PURPLE
    }

    public enum ColorValues {
        //因为声明的description是string，所以属性的值只能是String类型
        BLUE("蓝色"), YELLOW("黄色"), RED("红色"), GREEN("绿色"), WHITE("白色"), BRACK("黑色"), PURPLE(
                "紫色");
        private String description;

        //枚举的构造函数的修饰符只能用private
        private ColorValues(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
