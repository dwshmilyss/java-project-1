package com.yiban.javaBase.dev.data_structure.graph.traverse;

import com.yiban.javaBase.dev.data_structure.graph.store.Digraph;

/**
 * 深度优先遍历
 * 选定某个顶点 访问其某个邻接点 然后按照连通路径一条道走到黑 直到无连通为止
 * 然后回溯 再选定另外一个未被访问的邻接点 重复.....
 *
 * @auther WEI.DUAN
 * @date 2018/4/9
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DFSTraverse {
    private boolean[] visited;

    //从顶点index开始遍历
    public DFSTraverse(Digraph graph) {
        visited = new boolean[graph.getVertexNum()];
    }

    public static void main(String[] args) {
        int[][] data = {
                {0, 3},
                {1, 0},
                {1, 2},
                {2, 0},
                {2, 1},
        };
        Digraph digraph = new Digraph(data, 4);
        DFSTraverse dfsTraverse = new DFSTraverse(digraph);
        dfsTraverse.dfs(digraph, 1);
    }

    private void dfs(Digraph graph, int index) {
        visited[index] = true;
        System.out.println("graph = [" + graph + "], index = [" + index + "]");
        for (int i : graph.adj(index)) {
            if (!visited[i])
                dfs(graph, i);
        }
    }
}
