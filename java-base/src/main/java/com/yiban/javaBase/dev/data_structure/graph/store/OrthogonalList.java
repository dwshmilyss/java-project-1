package com.yiban.javaBase.dev.data_structure.graph.store;

/**
 * 有向图的十字链表实现
 * 十字链表创建图算法的时间复杂度和邻接表相同都为O(N + E)
 *
 * @auther WEI.DUAN
 * @date 2018/4/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class OrthogonalList {
    private VertexNode[] vertexs;
    private int vertexsNum;
    private int edgesNum;
    public OrthogonalList(int[][] data, int vertexsNum) {
        this.vertexsNum = vertexsNum;
        this.edgesNum = data.length;
        vertexs = new VertexNode[vertexsNum];
        for (int i = 0; i < vertexs.length; i++) {
            vertexs[i] = new VertexNode();
            vertexs[i].data = i;        //
        }

        //关键
        for (int i = 0; i < data.length; i++) {
            int tail = data[i][0];
            int head = data[i][1];
            EdgeNode out = vertexs[tail].firstOut;
            EdgeNode in = vertexs[head].firstIn;
            EdgeNode eNode = new EdgeNode(tail, head, in, out);
            vertexs[tail].firstOut = eNode;
            vertexs[head].firstIn = eNode;
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
        OrthogonalList orth = new OrthogonalList(data, 4);
        System.out.println("顶点1的出度为" + orth.outDegree(1));
        System.out.println("顶点1的入度为" + orth.inDegree(1));

    }

    //返回一个顶点的出度
    public int outDegree(int index) {
        int result = 0;
        EdgeNode current = vertexs[index].firstOut;
        while (current != null) {
            current = current.tailNext;
            result++;
        }
        return result;
    }

    //返回一个顶点的入度
    public int inDegree(int index) {
        int result = 0;
        EdgeNode current = vertexs[index].firstIn;
        while (current != null) {
            current = current.headNext;
            result++;
        }
        return result;
    }

    private class EdgeNode {
        //tailVex表示边的起点在顶点数组中的下标
        int tailVex;
        //出边表指针域，指向起点相同的下一条边。
        EdgeNode tailNext;
        //headVex表示边的终点在顶点数组中的下标
        int headVex;
        //入边表指针域，指向终点相同的下一条边。
        EdgeNode headNext;

        public EdgeNode(int tailVex, int headVex, EdgeNode headNext, EdgeNode tailNext) {
            super();
            this.tailVex = tailVex;
            this.headVex = headVex;
            this.headNext = headNext;
            this.tailNext = tailNext;
        }

    }

    private class VertexNode {
        //data表示顶点数据
        int data;
        //firstIn表示入边表（即是逆邻接表中的单链表）头指针
        EdgeNode firstIn;
        //firstOut表示出边表（即是邻接表中的单链表）头指针
        EdgeNode firstOut;
    }
}
