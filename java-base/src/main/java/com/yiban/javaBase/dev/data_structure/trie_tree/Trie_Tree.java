package com.yiban.javaBase.dev.data_structure.trie_tree;

/**
 * Created by Administrator on 2018/1/28 0028.
 */
public class Trie_Tree {
    private class Node {
        private int dumpli_num;//该字串的重复数目，该属性统计重复次数的时候有用,取值为0、1、2、3、4、5……
        private int prefix_num;//以该字串为前缀的字串数， 应该包括该字串本身
        private Node childs[];//此处用数组实现，当然也可以map或list实现以节省空间
        private boolean isLeaf;//是否为单词节点

        public Node() {
            dumpli_num = 0;
            prefix_num = 0;
            isLeaf = false;
            childs = new Node[26];
        }
    }

    public Trie_Tree() {
        ///初始化trie 树
        root = new Node();
    }

    private Node root;///树根

    /**
     * 插入字串，用循环代替迭代实现
     *
     * @param words
     */
    public void insert(String words) {
        insert(this.root, words);
    }

    private void insert(Node root, String words) {
        //首选转换为小写
        words = words.toLowerCase();
        //
        char[] chars = words.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            //用相对于a字母的值作为下标索引，也隐式地记录了该字母的值
            int index = chars[i] - 'a';
        }
    }

}
