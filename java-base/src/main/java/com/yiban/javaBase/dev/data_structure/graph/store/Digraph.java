package com.yiban.javaBase.dev.data_structure.graph.store;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 用邻接矩阵表示的有向图
 * 用一个一维数组表示顶点信息
 * 用一个二维数组表示弧信息 二维数组中非0的元素即为两个顶点的弧
 * <p/>
 * 优点：结构简单,操作方便
 * 缺点：对于稀疏图，这种实现方式将浪费大量的空间。
 *
 * @auther WEI.DUAN
 * @date 2018/4/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Digraph {
    /**
     * 顶点数
     */
    @Getter
    @Setter
    private int vertexNum;

    /**
     * 边个数
     */
    @Getter
    @Setter
    private int edgeNum;

    /**
     * 二维邻接矩阵 带方向的为弧
     * 从一个顶点出发的叫弧尾 tail
     * 到一个顶点的叫弧头 head
     */
    private int[][] arc;


    public Digraph(int[][] data, int vertexNum) {
        this.vertexNum = vertexNum;
        this.edgeNum = data.length;
        this.arc = new int[vertexNum][vertexNum];
        for (int i = 0; i < vertexNum; i++) {
            for (int j = 0; j < vertexNum; j++) {
                //这里如果是无向图可以用0 但是有向图因为可能会带有权重 而权重可能为0 所以这里用最大值表示
                arc[i][j] = Integer.MAX_VALUE;
            }
        }

        for (int i = 0; i < data.length; i++) {
            int tail = data[i][0];
            int head = data[i][1];
            arc[tail][head] = 1;
        }
    }

    public static void main(String[] args) {
        int[][] data = {
                {0, 3},
                {1, 0},
                {1, 2},
                {2, 0},
                {2, 1},
        };
        Digraph wd = new Digraph(data, 4);
        for (int i : wd.adj(1)) {
            System.out.println(i);
        }
    }

    //用于测试，返回一个顶点的邻接点
    public Iterable<Integer> adj(int vertex) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < vertexNum; i++) {
            if (arc[vertex][i] != Integer.MAX_VALUE)
                set.add(i);
        }
        return set;
    }

}
