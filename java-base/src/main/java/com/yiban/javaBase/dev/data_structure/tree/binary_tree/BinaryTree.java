package com.yiban.javaBase.dev.data_structure.tree.binary_tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Administrator on 2018/10/13 0013.
 */
public class BinaryTree<E extends Comparable> {

    private Node root = null;

    /**
     * 声明节点类
     */
    class Node {
        private E data;
        private Node leftChild;
        private Node rightChild;

        public Node(E data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "the data is : " + data;
        }
    }

    public BinaryTree(Node root) {
        this.root = root;
    }

    public BinaryTree() {
    }

    /**
     * 查找节点
     * 从root节点开始搜索
     * 1、查找值比当前节点值大，则搜索右子树
     * 2、查找值等于当前节点值，停止搜索（终止条件）
     * 3、查找值小于当前节点值，则搜索左子树
     */

    public Node find(E key) {
        Node current = root;
        while (current != null) {
            if (current.data.compareTo(key) > 0) {//当前值比查找值大，搜索左子树
                current = current.leftChild;
            } else if (current.data.compareTo(key) < 0) {//当前值比查找值小，搜索右子树
                current = current.rightChild;
            } else {
                return current;
            }
        }
        return null;//遍历完整个树没找到，返回null
    }

    //插入新节点
    public boolean insert(E data) {
        Node newNode = new Node(data);
        if (root == null) {//当前树为空，没有任何节点
            root = newNode;
            return true;
        } else {
            //第一次遍历 current指向root
            Node current = root;
            //定义父节点
            Node parentNode = null;
            while (current != null) {
                parentNode = current;//第一次遍历的时候，父节点指向root，随着遍历的进行，父节点逐渐向下一层移动
                if (current.data.compareTo(data) > 0) {//要插入的值比当前值小，则搜索左子树
                    current = current.leftChild;//当前节点指向左子树
                    if (current == null) {//如果左子树为空，那么可以插入当前值
                        parentNode.leftChild = newNode;//待插入数据放入左节点
                        return true;
                    }
                } else {//要插入的值比当前值大，则搜索右子树
                    current = current.rightChild;
                    if (current == null) {//右子节点为空，直接将新值插入到该节点
                        parentNode.rightChild = newNode;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //删除节点
    public boolean delete(E key) {
        Node current = root;
        Node parent = root;
        boolean isLeftChild = false;
        //查找删除值，找不到直接返回false
        while (current.data != key) {
            parent = current;
            if (current.data.compareTo(key) > 0) {
                isLeftChild = true;
                current = current.leftChild;
            } else {
                isLeftChild = false;
                current = current.rightChild;
            }
            if (current == null) {
                return false;
            }
        }
        //如果要删除的节点没有子节点
        if (current.leftChild == null && current.rightChild == null) {
            if (current == root) {//如果要删除的就是根节点
                root = null;
            } else if (isLeftChild) {//如果是左子节点，因为parent是current的父节点，所以直接把parent的left = null即可
                parent.leftChild = null;
            } else {//否则就把右子节点=null
                parent.rightChild = null;
            }
            return true;
        } else if (current.leftChild == null && current.rightChild != null) { //如果要删除的节点只有一个右子节点
            if (current == root) {//如果当前要删除的节点是root，那么因为root的右子节点不为Null，所以直接让root = current.rightChild即可
                root = current.rightChild;
            } else if (isLeftChild) {//否则，如果要删除的节点在左子树上，因为待删节点只有一个右子节点，所以直接赋值给待删节点的父节点的左子节点即可
                parent.leftChild = current.rightChild;
            } else {
                parent.rightChild = current.rightChild;
            }
        } else if (current.leftChild != null && current.rightChild == null) {//如果要删除的节点只有一个左子节点
            if (current == root) {
                root = current.leftChild;
            } else if (isLeftChild) {
                parent.leftChild = current.leftChild;
            } else {
                parent.rightChild = current.leftChild;
            }
        } else {
            //当前节点存在两个子节点
            /**
             * 　当删除的节点存在两个子节点，那么删除之后，两个子节点的位置我们就没办法处理了。既然处理不了，我们就想到一种办法，用另一个节点来代替被删除的节点
             * 因为二叉搜索树中的节点是按照关键字来进行排列的，某个节点的关键字次高节点是它的中序遍历后继节点（这里为什么是中序遍历呢：因为中序遍历总是先遍历左子树，然后根节点，然后右子树，这样就是一个从小到大的有序序列，所以某个节点的次高节点总是中序遍历后的该节点后续的第一个节点）。
             * 用后继节点来代替删除的节点，显然该二叉搜索树还是有序的。
             * 实际上就是要找比删除节点关键值大的节点集合中最小的一个节点，只有这样代替删除节点后才能满足二叉搜索树的特性。
             */
            Node successor = getSuccessor(current);
            if (current == root) {
                successor = root;
            } else if (isLeftChild) {
                parent.leftChild = successor;
            } else {
                parent.rightChild = successor;
            }
            successor.leftChild = current.leftChild;
        }
        return false;
    }

    public Node getSuccessor(Node delNode) {
        Node successorParent = delNode;
        Node successor = delNode;
        Node current = delNode.rightChild;
        while (current != null) {
            successorParent = successor;
            successor = current;
            current = current.leftChild;
        }
        //后继节点不是删除节点的右子节点，将后继节点替换删除节点
        if (successor != delNode.rightChild) {
            successorParent.leftChild = successor.rightChild;
            successor.rightChild = delNode.rightChild;
        }

        return successor;
    }

    /**
     * 中序遍历（先左子树，然后根节点，最后右子树）
     * 只有这种遍历是按顺序输出的
     */

    public void middleOrder(Node current) {
        if (current != null) {
            middleOrder(current.leftChild);
            System.out.print(current.data + " ");
            middleOrder(current.rightChild);
        }
    }

    /**
     * 前序遍历 先访问根节点 然后中序遍历左子树 最后中序遍历右子树
     */

    public void preOrder(Node current) {
        if (current != null) {
            System.out.print(current.data + " ");
            middleOrder(current.leftChild);
            middleOrder(current.rightChild);
        }
    }

    //后序遍历
    public void afterOrder(Node current) {
        if (current != null) {
            middleOrder(current.leftChild);
            middleOrder(current.rightChild);
            System.out.print(current.data + " ");
        }
    }

    //找到最大值
    public Node findMax() {
        Node current = root;
        Node maxNode = current;
        while (current != null) {
            maxNode = current;
            current = current.rightChild;
        }
        return maxNode;
    }

    //找到最小值
    public Node findMin() {
        Node current = root;
        Node minNode = current;
        while (current != null) {
            minNode = current;
            current = current.leftChild;
        }
        return minNode;
    }

    /**
     * 二叉树的逆转(递归实现)
     *
     * @param root
     */
    public Node invertNodeByRecursion(Node root) {
        if (root == null) {
            return root;
        }
        Node temp = root.leftChild;
        root.leftChild = invertNodeByRecursion(root.rightChild);
        root.rightChild = invertNodeByRecursion(temp);
        return root;
    }

    /**
     * 二叉树逆转 借助队列实现
     *
     * @param root
     * @return
     */
    public Node invertNode1(Node root) {
        if (root == null) return null;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            //加入到队列中
            Node current = queue.poll();
            //下面三行代码就是交换左右子节点
            Node temp = current.leftChild;
            current.leftChild = current.rightChild;
            current.rightChild = temp;
            //把左子节点加入到队列中 继续循环 直到左子树为空
            if (current.leftChild != null) {
                queue.add(current.leftChild);
            }
            //把右子节点加入到队列中 继续循环 直到右子树为空
            if (current.rightChild != null) {
                queue.add(current.rightChild);
            }
        }
        return root;
    }

    public static void main(String[] args) {
        BinaryTree<Integer> binaryTree = new BinaryTree<>();
        int[] datas = {5, 3, 1, 2, 6, 4, 7};
        for (int i = 0; i < datas.length; i++) {
            binaryTree.insert(datas[i]);
        }

        binaryTree.middleOrder(binaryTree.root);
        System.out.println("\n ================");
        binaryTree.preOrder(binaryTree.root);
        System.out.println("\n ================");
        binaryTree.afterOrder(binaryTree.root);
        System.out.println("\n ================");
//        binaryTree.delete(3);
        binaryTree.invertNodeByRecursion(binaryTree.root);
        binaryTree.middleOrder(binaryTree.root);
        System.out.println("\n ================");
        binaryTree.invertNode1(binaryTree.root);
        binaryTree.middleOrder(binaryTree.root);
        System.out.println("\n ================");
    }
}
