package com.yiban.javaBase.dev.data_structure.list;

import java.util.Random;

/**
 * 只有key的跳表，也就是按key找节点
 * <T extends Comparable<T>> 和 <T extends Comparable<? super T>> 有什么不同
 * <T extends Comparable<T>>
 *     类型 T 必须实现  Comparable 接口，并且这个接口的类型是 T。只有这样，T 的实例之间才能相互比较大小。
 *     例如，在实际调用时若使用的具体类是 Dog，那么 Dog 必须  implements Comparable<Dog>
 *
 * <T extends Comparable<? super T>>
 *     类型 T 必须实现  Comparable 接口，并且这个接口的类型是 T 或 T 的任一父类。这样声明后，T 的实例之间，T 的实例和它的父类的实例之间，可以相互比较大小。
 *     例如，在实际调用时若使用的具体类是 Dog (假设 Dog 有一个父类 Animal），Dog 可以从 Animal 那里继承  Comparable<Animal> ，或者自己  implements Comparable<Dog> 。
 * @param <T>
 */
public class SkipList_V<T extends Comparable<? super T>> {
    private int maxLevel;
    private SkipListNode<T>[] root;
    private int[] powers;
    private Random rd = new Random();

    SkipList_V() {
        this(4);
    }

    SkipList_V(int level) {
        maxLevel = level;
        root = new SkipListNode[maxLevel];
        powers = new int[maxLevel];
        for (int j = 0; j < maxLevel; j++)
            root[j] = null;
        choosePowers();
    }

    public boolean isEmpty() {
        return root[0] == null;
    }

    public void choosePowers() {
        powers[maxLevel-1] = (2 << (maxLevel-1)) - 1;    // 2^maxLevel - 1
        for (int i = maxLevel - 2, j = 0; i >= 0; i--, j++)
            powers[i] = powers[i+1] - (2 << j);           // 2^(j+1)
    }

    public int chooseLevel() {
        int i, r = Math.abs(rd.nextInt()) % powers[maxLevel-1] + 1;
        for (i = 1; i < maxLevel; i++)
            if (r < powers[i])
                return i-1; // return a level < the highest level;
        return i-1;         // return the highest level;
    }

    // make sure (with isEmpty()) that search() is called for a nonempty list;
    public SkipListNode search(T key) {
        int lvl;
        SkipListNode<T> prev, curr;            // find the highest nonnull
        for (lvl = maxLevel-1; lvl >= 0 && root[lvl] == null; lvl--); // level;
        prev = curr = root[lvl];
        while (true) {
            if (key.equals(curr.key))          // success if equal;
                return curr;
            else if (key.compareTo(curr.key) < 0) { // if smaller, go down,
                if (lvl == 0)                 // if possible
                    return null;
                else if (curr == root[lvl])   // by one level
                    curr = root[--lvl];      // starting from the
                else curr = prev.next[--lvl]; // predecessor which
            }                                  // can be the root;
            else {                             // if greater,
                prev = curr;                  // go to the next
                if (curr.next[lvl] != null)   // non-null node
                    curr = curr.next[lvl];   // on the same level
                else {                        // or to a list on a lower level;
                    for (lvl--; lvl >= 0 && curr.next[lvl] == null; lvl--);
                    if (lvl >= 0)
                        curr = curr.next[lvl];
                    else return null;
                }
            }
        }
    }

    public void insert(T key) {
        SkipListNode<T>[] curr = new SkipListNode[maxLevel];
        SkipListNode<T>[] prev = new SkipListNode[maxLevel];
        SkipListNode<T> newNode;
        int lvl, i;
        curr[maxLevel-1] = root[maxLevel-1];
        prev[maxLevel-1] = null;
        for (lvl = maxLevel - 1; lvl >= 0; lvl--) {
            while (curr[lvl] != null && curr[lvl].key.compareTo(key) < 0) {
                prev[lvl] = curr[lvl];           // go to the next
                curr[lvl] = curr[lvl].next[lvl]; // if smaller;
            }
            if (curr[lvl] != null && key.equals(curr[lvl].key)) // don't
                return;                          // include duplicates;
            if (lvl > 0)                         // go one level down
                if (prev[lvl] == null) {         // if not the lowest
                    curr[lvl-1] = root[lvl-1]; // level, using a link
                    prev[lvl-1] = null;        // either from the root
                }
                else {                           // or from the predecessor;
                    curr[lvl-1] = prev[lvl].next[lvl-1];
                    prev[lvl-1] = prev[lvl];
                }
        }
        lvl = chooseLevel();                // generate randomly level
        newNode = new SkipListNode<T>(key,lvl+1); // for newNode;
        for (i = 0; i <= lvl; i++) {        // initialize next fields of
            newNode.next[i] = curr[i];      // newNode and reset to newNode
            if (prev[i] == null)            // either fields of the root
                root[i] = newNode;         // or next fields of newNode's
            else prev[i].next[i] = newNode; // predecessors;
        }
    }

    public static void main(String[] args) {
        SkipList_V<String> skipList_v = new SkipList_V<>();
        skipList_v.insert("a");
        skipList_v.insert("b");
        skipList_v.insert("c");
        skipList_v.insert("d");
        skipList_v.insert("i");
        skipList_v.insert("k");
        skipList_v.insert("g");
        skipList_v.insert("j");
        skipList_v.insert("h");

        System.out.println("skipList_v = " + skipList_v.search("d").key);
    }

    static class SkipListNode<T extends Comparable<? super T>> {
        T key;
        /**
         * 普通的链表指针都是指向下一个节点
         *  跳表节点的指针指向的下一个节点数组
         *  为什么要指向一个数组呢，我想是因为当前节点可能指向下一层的自己节点
         */
        SkipListNode<T>[] next;


        public SkipListNode(T key, int level) {
            if (key == null) {
                throw new IllegalArgumentException("Key can not be null");
            }
            this.key = key;
            this.next = new SkipListNode[level];
        }

        public int compareKey(T k1) {
            return this.key.compareTo(k1);
        }
    }
}
