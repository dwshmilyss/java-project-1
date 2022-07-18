package com.yiban.javaBase.dev.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 有关字符串的一些算法题
 *
 * @auther WEI.DUAN
 * @date 2017/11/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class StringDemo {

    public static String res = "";

    public static void main(String[] args) {
        //求最长公共子串
        System.out.println(getLongestCommonString("ABCDEFG", "BABCD"));

        //求最长公共子序列
        String str1 = "BDCABA";
        String str2 = "ABCBDAB";
        int result = getLongestCommonSequenceLengthByDP(str1, str2);
        System.out.println(result);


        System.out.println("脚本之家测试结果：");
        String[] x = {" ", "A", "B", "C", "B", "D", "A", "B"};
        String[] y = {" ", "B", "D", "C", "A", "B", "A"};
        int[][] b = LCSLength(x, y);
        System.out.println("X和y的最长公共子序列是：");
        LCS(b, x, x.length - 1, y.length - 1);
        System.out.println();
        System.out.println(res);


        /**
         * 求最多公共子串的算法
         */
//        System.out.println(getLCSByDynamicProgramming("abcdefg", "123ab1cdfge"));
//        getAllCommonString("abcdefg".toCharArray(),"123ab1cdfge".toCharArray());

//        String str1 = new String("123456abcd567");
//        String str2 = new String("234dddabc45678");
//        String str1 = new String("abcd");
//        String str2 = new String("abcd");
//        getLCString(str1.toCharArray(), str2.toCharArray());

//        System.out.println(getFirstCharNotRepeatable("aabbcdcde"));


        findSubString("abcdefghi", "bca");
    }

    /**
     * 求2个字符串的最长公共子串（子串和子序列是2个不同的概念，子串必须是相邻的字符，子序列则按顺序，但是不需要相邻）
     * 例如： abdefgdefga 会被 abedefgdefaa
     * 最大公共子串是：defgdef
     * 最大公共子序列是：abdefgdefa
     * 思路：穷举法
     * 选取待比较的2个字符串中长度短的那个，然后从整个字符串开始(依次截掉最后一个字符)，在长的那个字符串中查找比较。
     * @param str1
     * @param str2
     * @return
     */
    private static String getLongestCommonString(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        int len1 = str1.length();
        int len2 = str2.length();
        String min, max, target;
        min = len1 <= len2 ? str1 : str2;
        max = len1 > len2 ? str1 : str2;
        //最外层：min子串的长度，从最大长度开始
        for (int i = min.length(); i >= 1; i--) {
            //遍历长度为i的min子串，从0开始
            for (int j = 0; j <= min.length() - i; j++) {
                target = min.substring(j, j + i);
                //遍历长度为i的max子串，判断是否与target子串相同，从0开始
                for (int k = 0; k <= max.length() - i; k++) {
                    if (max.substring(k, k + i).equals(target)) {
                        return target;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 求两个字符串的最长公共子序列的长度（动态规划算法）
     *
     * @param str1 注意传入的字符串前面要加个空格，因为算法中所有的循环都是从下标1开始的
     * @param str2 注意传入的字符串前面要加个空格，因为算法中所有的循环都是从下标1开始的
     * @return
     */
    private static int getLongestCommonSequenceLengthByDP(String str1, String str2) {
        int x = str1.length();
        int y = str2.length();
        int[][] c = new int[x][y];
        for (int row = 1; row < x; row++) {
            c[row][0] = 0;
        }
        for (int column = 1; column < y; column++) {
            c[0][column] = 0;
        }
        for (int i = 1; i < x; i++) {
            for (int j = 1; j < y; j++) {
                /**
                 * 为什么是这样的三个判断 这里面是有这样的公式的 因为是动态规划算法 会拆分成若干个子任务
                 * 所以会出现类似 ： i-1  j-1 这样的运算（因为i-1是i的子任务）
                 */
                if (str1.charAt(i) == str2.charAt(j)) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                } else if (c[i][j - 1] > c[i - 1][j]) {
                    c[i][j] = c[i][j - 1];
                } else {
                    c[i][j] = c[i - 1][j];
                }
            }
        }
        return c[x-1][y-1];
    }

    /**
     * 求两个字符串的最长公共子序列
     * @param x
     * @param y
     * @return
     */
    public static int[][] LCSLength(String[] x, String[] y) {
        int m = x.length;
        int n = y.length;
        int[][] b = new int[x.length][y.length];
        int[][] c = new int[x.length][y.length];
        for (int i = 1; i < m; i++) {
            c[i][0] = 0;
        }
        for (int i = 1; i < n; i++) {
            c[0][i] = 0;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (x[i] == y[j]) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    b[i][j] = 1;
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    b[i][j] = 2;
                } else {
                    c[i][j] = c[i][j - 1];
                    b[i][j] = 3;
                }
            }
        }
        return b;
    }

    public static void LCS(int[][] b, String[] x, int i, int j) {
        if (i == 0 || j == 0) {
            return;
        }
        if (b[i][j] == 1) {
            LCS(b, x, i - 1, j - 1);
            System.out.print(x[i] + " ");
            res = res.concat(x[i]);
        } else if (b[i][j] == 2) {
            LCS(b, x, i - 1, j);
        } else {
            LCS(b, x, i, j - 1);
        }
    }





    /**
     * 求最多公共子串 (动态规划)
     * 二维数组
     * 即找出两个字符串中所有的公共子串
     *
     * @param s
     * @param t
     * @return
     */
    private static String getLCSByDynamicProgramming(String s, String t) {
        int p = s.length();
        int q = t.length();

        //用一个二维数组来保存相同的字符所出现的位置
        String[][] num = new String[p][q];
        char char1, char2;

        int len = 0;
        String lcs = "";
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < q; j++) {
                char1 = s.charAt(i);
                char2 = t.charAt(j);
                if (char1 != char2) {
                    num[i][j] = "";
                } else {
                    if (i == 0) {
                        num[i][j] = String.valueOf(char1);
                    } else if (j == 0) {
                        num[i][j] = String.valueOf(char2);
                    } else {
                        num[i][j] = num[i - 1][j - 1] + String.valueOf(char1);
                    }

                    if (num[i][j].length() > len) {
                        len = num[i][j].length();
                        lcs = num[i][j];
                    } else if (num[i][j].length() == len) {
                        lcs = lcs + "," + num[i][j];
                    }
                }
            }
        }
        return lcs;
    }

    /**
     * 求2个字符串的所有公共子串
     *
     * @param str1
     * @param str2
     * @return
     */
    private static void getAllCommonString(char[] str1, char[] str2) {
        int i, j;
        //2个字符串的长度
        int len1, len2;
        len1 = str1.length;
        len2 = str2.length;
        //判断哪个更长
        int maxLen = len1 > len2 ? len1 : len2;
        int[] max = new int[maxLen];
        int[] maxIndex = new int[maxLen];
        // 记录对角线上的相等值的个数
        int[] c = new int[maxLen];

        for (i = 0; i < len2; i++) {
            for (j = len1 - 1; j >= 0; j--) {
                //如果str2的第一个字符 == str1的最后一个字符
                if (str2[i] == str1[j]) {
                    if ((i == 0) || (j == 0)) {
                        c[j] = 1;
                    } else {
                        c[j] = c[j - 1] + 1;
                    }
                } else {
                    c[j] = 0;
                }
                // 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
                if (c[j] > max[0]) {
                    // 记录对角线元素的最大值，之后在遍历时用作提取子串的长度
                    max[0] = c[j];
                    // 记录对角线元素最大值的位置
                    maxIndex[0] = j;

                    for (int k = 1; k < maxLen; k++) {
                        max[k] = 0;
                        maxIndex[k] = 0;
                    }
                    // 有多个是相同长度的子串
                } else if (c[j] == max[0]) {
                    for (int k = 1; k < maxLen; k++) {
                        if (max[k] == 0) {
                            max[k] = c[j];
                            maxIndex[k] = j;
                            break; // 在后面加一个就要退出循环了
                        }

                    }
                }
            }
        }

        for (j = 0; j < maxLen; j++) {
            if (max[j] > 0) {
                System.out.println("第" + (j + 1) + "个公共子串:");
                for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++){
                    System.out.print(str1[i]);
                }
                System.out.println(" ");
            }
        }
    }

    /**
     * 获取字符串中第一个没有重复的字符
     * 利用嵌套循环比较实现
     */
    public static Character getFirstCharNotRepeatable(String string) {
        if (string == null) {
            return null;
        }
        boolean repeated = false;
        for (int i = 0; i < string.length(); i++) {
            repeated = false;
            for (int j = 0; j < string.length(); j++) {
                if (i != j && string.charAt(i) == string.charAt(j)) {
                    repeated = true;
                    break;
                }
            }
            if (!repeated) {
                return string.charAt(i);
            }
        }

        return null;
    }


    /**
     * 获取字符串中第一个没有重复的字符
     * ""
     */
    public static Character getFirstCharNotRepeatableByMap(String string) {
        if (string == null) {
            return null;
        }
        //因为是要输出第一个不重复的字符，所以这里要用LinkedHashMap
        Map<Character, Integer> map = new LinkedHashMap<>();
        for (char c :
                string.toCharArray()) {
            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                map.put(c, map.get(c) + 1);
            }
        }

        for (Map.Entry<Character, Integer> en :
                map.entrySet()) {
            if (en.getValue() == 1) {
                return en.getKey();
            }
        }
        return null;
    }

    /**
     * 查找子串第一次出现的位置
     * @param str1
     * @param str2
     * @return
     */
    public static int findSubString(String str1,String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        int len1 = str1.length();
        int len2 = str2.length();
        int i = 0, j = 0;
        String min,max;
        max = len1 < len2 ? str2 : str1;
        min = len1 < len2 ? str1 : str2;
        while (i < max.length() && j < min.length()) {
            if (max.charAt(i) == min.charAt(j)) {
                i++;
                j++;
            } else {
                i = i - j + 1;
                j = 0;
            }
        }
        if (j >= min.length()) { //子串遍历完成则返回匹配成功时 i 的起始位置。
            return i - j;
        } else {   // 没找到
            return -1;
        }

    }
}

