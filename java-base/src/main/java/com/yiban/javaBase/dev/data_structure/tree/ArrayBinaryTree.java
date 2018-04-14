package com.yiban.javaBase.dev.data_structure.tree;

/**
 * 顺序存储的二叉树
 *
 * @auther WEI.DUAN
 * @date 2018/4/13
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ArrayBinaryTree<E> {
    private static final int DEFAULT_DEPTH = 5;

    private int size = 0;
    private E[] datas;

    public ArrayBinaryTree() {
        this(DEFAULT_DEPTH);
    }

    public ArrayBinaryTree(int depth) {
        datas = (E[]) new Object[(int) Math.pow(2, depth)];
    }

    public static void main(String[] args) {
        char[] data = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        ArrayBinaryTree<Character> abt = new ArrayBinaryTree<>();
        for (int i = 0; i < data.length; i++) {
            abt.add(data[i]);
        }
        System.out.print(abt.getParent(abt.indexOf('J')));
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public E getRoot() {
        //根节点是从数组下标1开始的，0不存储节点
        return datas[1];
    }

    //检查下标是否越界
    private void checkIndex(int index) {
        if (index <= 0 || index >= datas.length) {
            throw new IndexOutOfBoundsException();
        }
    }

    // 返回指定节点的父节点
    public E getParent(int index) {
        checkIndex(index);
        if (index == 1) {
            throw new RuntimeException("根节点不存在父节点！");
        }
        return datas[index / 2];
    }

    //获取右子节点
    public E getRight(int index) {
        checkIndex(index * 2 + 1);
        return datas[index * 2 + 1];
    }

    //获取左子节点
    public E getLeft(int index) {
        checkIndex(index * 2);
        return datas[index * 2];
    }

    //返回指定数据的位置
    public int indexOf(E data) {
        if (data == null) {
            throw new NullPointerException();
        } else {
            for (int i = 0; i < datas.length; i++) {
                if (data.equals(datas[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    //顺序添加元素
    public void add(E element) {
        checkIndex(size + 1);
        datas[size + 1] = element;
        size++;
    }

    //在指定位置添加元素
    public void add(E element, int parent, boolean isLeft) {

        if (datas[parent] == null) {
            throw new RuntimeException("index[" + parent + "] is not Exist!");
        }
        if (element == null) {
            throw new NullPointerException();
        }

        if (isLeft) {
            checkIndex(2 * parent);
            if (datas[parent * 2] != null) {
                throw new RuntimeException("index[" + parent * 2 + "] is  Exist!");
            }
            datas[2 * parent] = element;
        } else {
            checkIndex(2 * parent + 1);
            if (datas[(parent + 1) * 2] != null) {
                throw new RuntimeException("index[" + parent * 2 + 1 + "] is  Exist!");
            }
            datas[2 * parent + 1] = element;
        }
        size++;
    }
}
