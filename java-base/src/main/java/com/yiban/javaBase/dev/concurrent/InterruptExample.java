package com.yiban.javaBase.dev.concurrent;

/**
 *
 */
public class InterruptExample implements Runnable {

    /**
     * 打印：
     * 主线程开始休眠...
     * Thread is running...
     * Thread is running...
     * Thread is running...
     * 主线程结束休眠...
     * Thread was interrupted during sleep.
     * Thread exiting...
     *
     * 因为是main开始执行，所以一定是主线程先执行，主线程sleep的时候让出cpu，然后子线程执行
     * 子线程先判断是否已经中断，没有中断，进入while循环，
     * 这里会打印3次 Thread is running... 是因为虽然子线程sleep了，但是只sleep 1s，而同时主线程也在sleep 3s，
     * 这时子线程休眠1s结束，依然重新获取了cpu，但是3s过后主线程苏醒，重新获取了cpu，所以这边子线程会打印3次
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 执行某些操作
                System.out.println("Thread is running...");
                Thread.sleep(1000); // 可能会抛出 InterruptedException
            }
        } catch (InterruptedException e) {
            // 处理中断
            System.out.println("Thread was interrupted during sleep.");
        } finally {
            // 清理资源或执行其他操作
            System.out.println("Thread exiting...");
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new InterruptExample());
        thread.start();

        // 主线程休眠一段时间，然后中断
        try {
            System.out.println("主线程开始休眠...");
            Thread.sleep(4000);
            System.out.println("主线程结束休眠...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 中断线程
        thread.interrupt();
    }
}
