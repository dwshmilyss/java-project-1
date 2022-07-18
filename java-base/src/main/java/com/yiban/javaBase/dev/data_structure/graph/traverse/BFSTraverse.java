package com.yiban.javaBase.dev.data_structure.graph.traverse;

import com.yiban.javaBase.dev.data_structure.graph.store.AdjListDigraph;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 广度优先遍历
 * 选定一个顶点 遍历访问其所有的邻接点 然后选定某一个邻接点 再遍历其所有....重复直到所有顶点都被访问
 *
 * @auther WEI.DUAN
 * @date 2018/4/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class BFSTraverse {
    private boolean[] visited;

    public BFSTraverse(AdjListDigraph graph) {
        visited = new boolean[graph.getVertexsNum()];
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
        BFSTraverse bfsTraverse = new BFSTraverse(ald);
        bfsTraverse.bfs(ald, 1);
    }

    private void bfs(AdjListDigraph graph, int index) {
        //在JSE中LinkedList实现了Queue接口
        Queue<Integer> queue = new LinkedList<>();
        visited[index] = true;
        System.out.println("graph = [" + graph + "], index = [" + index + "]");
        queue.add(index);
        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            for (int i : graph.adj(vertex)) {
                if (!visited[i]) {
                    visited[i] = true;
                    queue.offer(i);
                }
            }
        }
    }
}
