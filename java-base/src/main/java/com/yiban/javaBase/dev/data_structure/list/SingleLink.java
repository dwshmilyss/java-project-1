package com.yiban.javaBase.dev.data_structure.list;

import java.util.Stack;

/**
 * @auther WEI.DUAN
 * @date 2018/11/16
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SingleLink<E extends Comparable> {
    class Node {
        E data;
        Node next;

        public Node(E data) {
            this.data = data;
        }
    }

    Node head;

    public SingleLink() {
        //默认头结点不存储任何数据 只用作指针指向第一个有数据的节点
        this.head = new Node(null);
    }

    public E insert(E data) {
        Node current = new Node(data);
        Node temp = head;
        //遍历找到最后一个节点
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = current;
        return data;
    }

    //递归遍历单链表
    public void iteratorByRecursion(Node current) {
        if (current.next == null) {
            return;
        }
        System.out.print(current.next.data + " ");
        iteratorByRecursion(current.next);
    }

    public void iterator() {
        Node temp = head;
        if (temp == null) {
            return;
        }
        while (temp.next != null) {
            //注意 这个顺序不能颠倒 一定要先找到下一个节点，再输出，否则会丢失最后一个节点
            temp = temp.next;
            System.out.print(temp.data + " ");
        }
    }

    public int getLength() {
        int len = 0;
        Node temp = head;
        while (temp.next != null) {
            temp = temp.next;
            len++;
        }
        return len;
    }

    /**
     * 删除某个位置的元素
     *
     * @param index 从1开始
     * @return
     */
    public E removeByIndex(int index) {
        if (index <= 0 || index > getLength()) {
            System.out.println("删除位置不合法");
            return null;
        }
        Node temp = head;
        int len = 0;
        while (temp.next != null) {
            len++;
            if (index == len) {
                Node current = temp.next;
                E data = current.data;
                temp.next = temp.next.next;
                current = null;
                return data;
            } else {
                temp = temp.next;
            }
        }
        return null;
    }

    /**
     * 插入某个位置
     *
     * @param index
     * @param data
     * @return
     */
    public E insertByIndex(int index, E data) {
        if (index <= 0 || index > getLength()) {
            System.out.println("删除位置不合法");
            return null;
        }
        Node temp = head;
        int len = 0;
        while (temp.next != null) {
            len++;
            if (index == len) {
                Node current = new Node(data);
                current.next = temp.next;
                temp.next = current;
                return data;
            } else {
                temp = temp.next;
            }
        }
        return null;
    }


    public void reverseSingleLink() {
        if (head == null || head.next == null) {
            return;
        }
        //声明两个指针  分别指向第一个和第二个节点
        Node p = head.next;
        Node q = head.next.next;
        //将第一个结点的next置为空，否则会出现一个环
        Node temp = null;
        //循环结束后 q==null 那么p就指向原链表的最后一个节点
        while (q != null) {
            //第二个指针往后移
            temp = q.next;
            q.next = p;
            p = q;
            q = temp;
        }
        //循环结束后 这里要把原链表的第一个节点到第二个节点的指针打断，不然在第一个节点和第二个节点之间会形成一个循环引用 即 1->2,2->1
        head.next.next = null;
        //更改头结点
        head.next = p;
    }


    /**
     * 借助栈实现逆转链表(注意：这个方法并没有改变原来的链表，只是把数据放入了栈中)
     */
    public void reverseByStack() {
        Stack<Node> stack = new Stack<Node>();
        Node node = head.next;
        while (node != null) {
            stack.push(node);
            node = node.next;
        }
        while (stack.size() > 0) {
            node = stack.pop();
            System.out.print(node.data + "->");
        }
        // 在最后添加上null作为结束标志
        System.out.println("null");
    }

    /**
     * 递归实现逆转链表(这个有点绕)
     * 简单来讲 就是划分子任务
     */
    public Node reverseByRecurtion(Node head) {
        //这里的作用是要找到尾节点
        if (head == null || head.next == null) {
            return head;
        }
        /**
         * 递归调用在中间 是要在找到尾节点之后将控制权交给上一次函数 比如链表顺序为4->3->2->1
         * 则这里第一次开始递归的时候head为2 因为head=1的时候上面的判断成立，调用head.next=1的就是head=2 (因为参数是head.next嘛)
         */
        Node nowHead = reverseByRecurtion(head.next);
        //这里刚好是倒数第二个节点的时候，所以输出是head.next.data
        System.out.print(head.next.data+"->");
        //向后移动head，网上的代码有这一步，但是我觉得没什么用，而且测试了可以去掉
//        head.next.next = head;
        //每次去掉一个节点（已经输出过了，生成新的子任务）
        head.next = null;
        return nowHead;
    }

    public static void main(String[] args) {
        int[] datas = {1, 2, 3, 4};
        SingleLink singleLink = new SingleLink();
        for (int i = 0; i < datas.length; i++) {
            singleLink.insert(datas[i]);
        }
        singleLink.iterator();

//        System.out.println("\n =============== ");
//        System.out.println("len is = " + singleLink.getLength());
//        System.out.println("=============== ");
//        System.out.println(singleLink.removeByIndex(5));
//        System.out.println("len is = " + singleLink.getLength());
//        singleLink.iterator();
//        System.out.println("\n =============== ");
//        System.out.println(singleLink.insertByIndex(5, 2));
//        System.out.println("len is = " + singleLink.getLength());
//        singleLink.iterator();
        System.out.println("\n reverseSingleLink =============== ");
        singleLink.reverseSingleLink();
//        singleLink.iteratorByRecursion(singleLink.head);
        singleLink.iterator();
//        System.out.println("\n reverseByStack =============== ");
//        singleLink.reverseByStack();
//        System.out.println("\n reverseByRecurtion =============== ");
//        singleLink.reverseByRecurtion(singleLink.head);
//        singleLink.iterator();
    }

}