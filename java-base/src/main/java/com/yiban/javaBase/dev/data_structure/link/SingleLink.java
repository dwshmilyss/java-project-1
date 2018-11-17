package com.yiban.javaBase.dev.data_structure.link;

/**
 * @auther WEI.DUAN
 * @date 2018/11/16
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SingleLink<E> {
    class Node<E>{
        E data;
        Node next;

        public Node(E data) {
            this.data = data;
        }
    }

    Node head;
    Node tail;

    public E insert(E data){
        if (head == null){
            head = new Node(data);
            tail = head;
        }else {
            head.next = new Node(data);
            tail = head.next;
        }
        return data;
    }

}