package com.yiban.javaBase.dev.data_structure.list;

import java.util.Random;

/**
 * 跳表 K,V版本
 *
 * 初学者建议去如下版本学习.
 * git checkout `70ef2f5`
 *
 * 1. 排序的顺序.
 * 目前只能是从小到大排序。
 * 2. 跳表的层数和数据量的最优解。
 * 即 数据量和层数的系数比.或者说映射关系.
 * 3. 双向的跳表的实现?
 * 4. 增加跨度字段?
 *
 * @author
 * @date 2020-01-05
 * @since 1.0.0
 */
public class SkipList_KV<K extends Comparable<K>,V> {
    //16
    public static final int MAX_VALUE = 1 << 4;

    /**
     * 头结点
     */
    public Node<K, V> head;

    /**
     * 最大层数
     */
    private int maxLevel = 1;

    public SkipList_KV() {
        head = new Node<>();
    }

    public SkipList_KV(K key, V value) {
        this.head = new Node<>();
        insert(key, value);
    }

    public SkipList_KV(int maxLevel) {
        head = new Node<>();
        this.maxLevel = maxLevel;
    }

    /**
     * 生成一个随机的层数
     * @return 层数
     */
    private int randLevel() {
        int level = new Random().nextInt(MAX_VALUE) + 1;
        System.out.println("生成的层数为 = " + level);
        return level;
    }

    public boolean insert(K key, V value) {
        // 生成一个新节点的层
        int newNodeLevel = randLevel();
        //待插入节点的每一层的前驱节点
        Node<K, V>[] previousNodes = new Node[this.maxLevel];

        // one. 查询前继节点和目标节点
        Node<K, V> q = genericSearch(key, previousNodes);

        /// key 相等, 更新value, 直接返回。
        if (null != q && q.compareKey(key) == 0) {
            q.value = value;
            return true;
        }

        // two. 生成一个新的Node
        Node<K, V> newNode = new Node<>(key, value, newNodeLevel);

        // three. 对于每一层, 像普通链表一样插入新节点。
        int l = newNodeLevel - 1;

        if (newNodeLevel >= this.maxLevel) {
            for (; l >= this.maxLevel; l--) {
                this.head.next[l] = newNode;
            }
            this.maxLevel = newNodeLevel;
        }

        for (; l >= 0; l--) {
            newNode.next[l] = previousNodes[l].next[l];
            previousNodes[l].next[l] = newNode;
        }
        return true;
    }

    /**
     * 删除指定的key的元素
     *
     * @param key Key
     * @return 对应的值, 不存在返回 null;
     */
    public V delete(K key) {

        Node<K, V>[] previousNodes = new Node[this.maxLevel];
        Node<K, V> kvNode = genericSearch(key, previousNodes);

        if(kvNode == null){
            return null;
        }
        for (int i = previousNodes.length - 1; i >= 0; i--) {
            if (kvNode == previousNodes[i].next[i]) {
                previousNodes[i].next[i] = kvNode.next[i];
                if (this.head.next[i] == null) {
                    this.maxLevel--;
                }
            }
        }

        return kvNode.value;
    }

    /**
     * 查找
     *
     * @param key Key
     * @return 存在, 返回value，不存在,返回null。
     */
    public V search(K key) {
        Node<K, V> kvNode = genericSearch(key, null);
        return null == kvNode ? null : kvNode.value;
    }

    /**
     * 通用的查询实现
     *
     * @param key           Key
     * @param previousNodes 前继节点
     * @return 存在返回Node，不存在返回Null
     */
    private Node<K, V> genericSearch(K key, Node<K, V>[] previousNodes) {
        Node<K, V> q = null, p = this.head;
        if (null == previousNodes) {
            previousNodes = new Node[this.maxLevel];
        }
        /**
         * 很奇怪这里没有等于key的运算出现？
         * 原因是这里 q = p.next[level] && q.compareKey(key) < 0
         * 因为链表每一层都是有序的，所以要找的元素节点一定是q.compareKey(key) < 0之后的next节点
         * 直到遍历所有的层，最后停留的q节点就是要找的节点。
         * 这里还可以优化一下
         */
        int cn  = 0;
        for (int level = this.maxLevel - 1; level >= 0; level--) {
            while (null != (q = p.next[level]) && q.compareKey(key) < 0) {
                p = q;
            }
            previousNodes[level] = p;
            cn++;
            //这里是我加的优化，这样如果提前找到了要找的节点，就不用遍历所有的层
            if (q != null && q.compareKey(key) == 0) {
                break;
            }
        }
        System.out.println("cn = " + cn);
        if (null == q || q.compareKey(key) > 0) {
            return null;
        }
        return q;
    }


    /**
     * 打印生成的跳表
     */
    public void show() {
        System.out.println("-------------------------");
        /**
         * 其实insert完就是一个矩阵，每一行的数据都一样，只不过对于每个插入的数据，其随机到的层数是不一样的，
         * 所以这里在打印的时候，由于每个数据的层数不一样，这里也就会出现每层数据不一样的情况
         * 例如 数据a 随机到了16层(最大层数)
         * 那么从16层开始每一层都有a
         * 下一个数据 b 随机到了14层，则在循环到15层的时候就没有b这个数据了
         */
        for (int level = this.maxLevel - 1; level >= 0; level--) {
            System.out.printf("level: %d\t", level);
            Node<K, V> q = this.head.next[level];
            while (null != q) {
                System.out.print("(" + q.key + "," + q.value + ")\t");
                q = q.next[level];
            }
            System.out.println();
        }
        System.out.println("-------------------------");
    }


    public static void main(String[] args) {
        SkipList_KV skipList = new SkipList_KV(4);
        skipList.insert("a", 1);
        skipList.insert("e", 5);
        skipList.insert("f", 6);
        skipList.insert("g", 7);
        skipList.insert("h", 8);
        skipList.insert("b", 2);
        skipList.insert("c", 3);
        skipList.insert("d", 4);


        skipList.show();

        System.out.println("search result = " + skipList.search("d"));
    }

    static class Node<K extends Comparable<K>, V> {
        K key;
        V value;
        /**
         * 普通的链表指针都是指向下一个节点
         *  跳表节点的指针指向的下一个节点数组
         *  为什么要指向一个数组呢，我想是因为当前节点可能指向下一层的自己节点
         */
        Node<K,V>[] next;

        private Node() {
            this.next = new Node[MAX_VALUE];
        }

        public Node(K key, V value, int level) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Key and Value can not be null");
            }
            this.key = key;
            this.value = value;
            this.next = new Node[level];
        }

        public int compareKey(K k1) {
            return this.key.compareTo(k1);
        }
    }
}
