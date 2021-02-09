package com.yiban.javaBase.dev.data_structure.list;

/**
 * @auther WEI.DUAN
 * @date 2019/4/16
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ReverseLink {
    static class Node<T>{
        ReverseLink.Node next;
        T value;

        public Node(ReverseLink.Node next, T value) {
            this.next = next;
            this.value = value;
        }
    }

    static ReverseLink.Node reverseByIterator(ReverseLink.Node head){
        if (head == null || head.next == null){
            return null;
        }
        Node pre = null;
        Node temp = null;

        while (head != null){
            temp = head.next;
            head.next = pre;
            pre = head;
            head = temp;
//            print(pre.next);
        }

        return pre;
    }

    /**
     * 如果头结点不包含数据的话  用这个输出方法（因为头结点不输出）。但是反转后的输出方法要用下面的
     * @param head
     */
    static void print(ReverseLink.Node head){
        Node temp = head;
        while (temp.next != null){
            temp = temp.next;
            System.out.print("-->" + temp.value.toString());
        }
        System.out.println();
    }

    /**
     * 注意反转后要从第一个元素开始输出（注意和反转前做对比，但是如果连头结点都输出的话，那么反转前后都用这一个方法即可）
     * @param head
     */
    static void reversePrint(ReverseLink.Node head){
        Node temp = head;
        while (temp != null){
            System.out.print("-->" + temp.value.toString());
            temp = temp.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ReverseLink.Node<Integer> node5 = new ReverseLink.Node<>(null,5);
        ReverseLink.Node<Integer> node4 = new ReverseLink.Node<>(node5,4);
        ReverseLink.Node<Integer> node3 = new ReverseLink.Node<>(node4,3);
        ReverseLink.Node<Integer> node2 = new ReverseLink.Node<>(node3,2);
        ReverseLink.Node<Integer> node1 = new ReverseLink.Node<>(node2,1);
        ReverseLink.Node<Integer> head = new ReverseLink.Node<>(node1,0);
        reversePrint(head);
        System.out.println("==== reverse =======");
        Node reverseNode = reverseByIterator(head);
        reversePrint(reverseNode);
    }
}