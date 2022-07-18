package com.yiban.javaBase.dev.data_structure.graph.store;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 用邻接表表示的有向图
 * <p/>
 * 这里只构造了邻接表  没有构造逆邻接表
 * <p/>
 * 将图中顶点用一个一维数组存储，每个顶点Vi的所有邻接点用一个单链表来存储。
 * 有向图的邻接表是以顶点为弧尾来存储边表的，这样很容易求一个顶点的出度（顶点对应单链表的长度），但若求一个顶点的入度，则需遍历整个图才行。
 * 这时可以建立一个有向图的逆邻接表即对每个顶点v都建立一个弧头尾v的单链表。
 * <p/>
 * 本算法的时间复杂度为 O(N + E)，其中N、E分别为顶点数和边数，邻接表实现比较适合表示稀疏图。
 *
 * @auther WEI.DUAN
 * @date 2018/4/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class AdjListDigraph {
    private VertexNode[] vertexs;
    /**
     * 顶点数
     */
    @Getter
    @Setter
    private int vertexsNum;
    /**
     * 边数
     */
    @Getter
    @Setter
    private int edgesNum;

    public AdjListDigraph(int[][] data, int vertexsNum) {
        this.vertexsNum = vertexsNum;
        this.edgesNum = data.length;
        vertexs = new VertexNode[vertexsNum];
        for (int i = 0; i < vertexs.length; i++) {
            vertexs[i] = new VertexNode();
            vertexs[i].data = i;        //
        }

        for (int i = 0; i < data.length; i++) {
            int index = data[i][1];
            EdgeNode next = vertexs[data[i][0]].headNode;
            EdgeNode eNode = new EdgeNode(index, next);
            //头插法 新增的弧插入链表的头部
            vertexs[data[i][0]].headNode = eNode;
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
        AdjListDigraph ald = new AdjListDigraph(data, 4);
        for (int i : ald.adj(1)) {
            System.out.println(i);
        }
    }

    //用于测试，返回一个顶点的邻接点
    public Iterable<Integer> adj(int index) {
        Set<Integer> set = new HashSet<>();
        EdgeNode current = vertexs[index].headNode;
        while (current != null) {
            VertexNode node = vertexs[current.index];
            set.add(node.data);
            current = current.next;
        }
        return set;
    }

    private class EdgeNode {
        int index;
        EdgeNode next;

        /**
         * @param index 弧头
         * @param next
         */
        EdgeNode(int index, EdgeNode next) {
            this.index = index;
            this.next = next;
        }
    }

    private class VertexNode {
        int data;
        EdgeNode headNode;
    }
}
