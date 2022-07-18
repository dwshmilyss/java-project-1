package com.yiban.javaBase.dev.data_structure.tree.trie_tree;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/1/28 0028.
 */
public class Trie_Tree {
    private Node root;///树根

    public Trie_Tree() {
        ///初始化trie 树
        root = new Node();
    }

    public static void main(String[] args) {
        Trie_Tree trie = new Trie_Tree();
        trie.insert("I");
        trie.insert("Love");
        trie.insert("China");
        trie.insert("China");
        trie.insert("China");
        trie.insert("China");
        trie.insert("China");
        trie.insert("xiaoliang");
        trie.insert("xiaoliang");
        trie.insert("man");
        trie.insert("handsome");
        trie.insert("love");
        trie.insert("chinaha");
        trie.insert("her");
        trie.insert("know");

        HashMap<String, Integer> map = trie.getAllWords();

        for (String key : map.keySet()) {
            System.out.println(key + " 出现: " + map.get(key) + "次");
        }


        map = trie.getWordsForPrefix("chin");

        System.out.println("\n\n包含chin（包括本身）前缀的单词及出现次数：");
        for (String key : map.keySet()) {
            System.out.println(key + " 出现: " + map.get(key) + "次");
        }

        if (trie.isExist("xiaoming") == false) {
            System.out.println("\n\n字典树中不存在：xiaoming ");
        }
    }

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
        for (int i = 0, length = chars.length; i < length; i++) {
            //用相对于a字母的值作为下标索引，也隐式地记录了该字母的值
            int index = chars[i] - 'a';
            if (root.childs[index] != null) {//如果子节点存在 则前缀个数+1
                root.childs[index].prefix_num++;
            } else {//否则就new一个新的子节点
                root.childs[index] = new Node();
                root.childs[index].prefix_num++;
            }

            //如果到了字符串结尾，则标记
            if (i == length - 1) {
                root.childs[index].isLeaf = true;
                root.childs[index].dumpli_num++;
            }
            //root指向当前子节点，继续处理(即当前子节点作为新的root，这样才能组成链表)
            root = root.childs[index];

        }
    }

    /**
     * 遍历字典树，查找所有的字符串及其所出现的次数
     *
     * @return
     */
    public HashMap<String, Integer> getAllWords() {
        return preTraversal(this.root, "");
    }

    /**
     * 前序遍历
     *
     * @param root    子树根节点
     * @param prefixs 查询到该节点前所遍历过的前缀
     * @return
     */
    private HashMap<String, Integer> preTraversal(Node root, String prefixs) {
        HashMap<String, Integer> map = new HashMap<>();
        if (root != null) {
            if (root.isLeaf == true) {
                //当前即为一个单词
                map.put(prefixs, root.dumpli_num);
            }
        }
        //因为英文字母有26个，所以这里的length就是26
        for (int i = 0, length = root.childs.length; i < length; i++) {
            if (root.childs[i] != null) {
                //因为存储的时候是相对于字符a存入的，所以检索的时候也要+'a'，其实这里就是abcdefg....26个英文字符
                char ch = (char) (i + 'a');
                //拼接成新的字符串 递归用
                String tempStr = prefixs + ch;
                map.putAll(preTraversal(root.childs[i], tempStr));
            }

        }

        return map;
    }

    /**
     * 判断某个字符串是否在字典树中
     *
     * @param word
     * @return
     */
    public boolean isExist(String word) {
        return search(this.root, word);
    }

    private boolean search(Node root, String word) {
        char[] chs = word.toLowerCase().toCharArray();
        for (int i = 0, length = chs.length; i < length; i++) {
            int index = chs[i] - 'a';
            if (root.childs[index] == null) {
                return false;
            }
            root = root.childs[index];
        }
        return true;
    }

    /**
     * 获取以某个字符串为前缀的所有字符串 类似百度搜索条里的智能提示
     *
     * @param prefix
     * @return
     */
    public HashMap<String, Integer> getWordsForPrefix(String prefix) {
        return getWordsForPrefix(this.root, prefix);
    }

    private HashMap<String, Integer> getWordsForPrefix(Node root, String prefix) {
        HashMap<String, Integer> map = new HashMap<>();
        char[] chars = prefix.toLowerCase().toCharArray();
        for (int i = 0, length = chars.length; i < length; i++) {
            //找到待查询前缀里每一个字符所在字典树中索引的位置(相对于字符a)
            int index = chars[i] - 'a';
            if (root.childs[index] == null) {
                return null;
            }
            //以最后一个字符所在的Node为root
            root = root.childs[index];
        }
        //结果包含该字符串本身
        /**
         * 这里传入的root是最后一个待查询字符所在的节点，因为是查找前缀，所以只需要查找其后面的字符即可
         */
        return preTraversal(root, prefix);
    }

    private class Node {
        private int dumpli_num;//该字串的重复数目，该属性统计重复次数的时候有用,取值为0、1、2、3、4、5……
        private int prefix_num;//以该字串为前缀的字串数， 应该包括该字串本身
        private Node childs[];//此处用数组实现，当然也可以map或list实现以节省空间
        private boolean isLeaf;//是否为单词节点

        public Node() {
            dumpli_num = 0;
            prefix_num = 0;
            isLeaf = false;
            //每个节点都有指向26个英文字母数组节点的指针，就是这个原因导致空间复杂度高
            childs = new Node[26];
        }
    }
}
