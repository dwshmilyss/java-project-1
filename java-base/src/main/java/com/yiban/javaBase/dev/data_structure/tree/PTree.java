package com.yiban.javaBase.dev.data_structure.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 树的两种表示方法
 *
 * @auther WEI.DUAN
 * @date 2018/4/13
 * @website http://blog.csdn.net/dwshmilyss
 */
public class PTree<E> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PTree.class);
    private static final int DEFAULT_CAPACITY = 100;
    private int size;
    private Node[] nodes;

    public PTree() {
        nodes = new PTree.Node[DEFAULT_CAPACITY];
    }

    public static void main(String[] args) {
        LOGGER.warn("Hello World!");
    }

    /**
     * 双亲表示法
     * 特点：在O(1)的时间内就可以找到双亲节点 但是要找孩子节点则需要遍历整个树
     */
    private class Node {
        E data;
        int parent;

        Node(E data, int parent) {
            this.data = data;
            this.parent = parent;
        }
    }

    /**
     * 孩子表示法  有最多孩子的创建几个node
     * 浪费空间
     */
    private class CNode {
        E data;
        Node child1;
        Node child2;
        Node childn;
    }
}
