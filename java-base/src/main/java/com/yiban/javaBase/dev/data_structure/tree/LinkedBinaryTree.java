package com.yiban.javaBase.dev.data_structure.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 链表存储的二叉树
 *
 * @auther WEI.DUAN
 * @date 2018/4/13
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LinkedBinaryTree<E> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedBinaryTree.class);
    private List<Node> nodeList = null;

    public static void main(String[] args) {
        LOGGER.trace("=====trace=====");
        LOGGER.debug("=====debug=====");
        LOGGER.info("=====info=====");
        LOGGER.warn("=====warn=====");
        LOGGER.error("=====error=====");
    }

    public Node getRoot() {
        return nodeList.get(0);
    }

    private class Node {
        Node leftChild;
        Node rightChild;
        E data;

        public Node(E data) {
            this.data = data;
        }
    }

}
