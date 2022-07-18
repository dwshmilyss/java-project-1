package com.yiban.javaBase.dev.collections.queue;

import org.springframework.util.StopWatch;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 模拟考试交卷
 * 模拟一个考试的日子，考试时间为120分钟，30分钟后才可交卷，当时间到了，或学生都交完卷了考试结束。
 * 这个场景中几个点需要注意：
 * 1、考试时间为120分钟，30分钟后才可交卷，初始化考生完成试卷时间最小应为30分钟
 * 2、对于能够在120分钟内交卷的考生，如何实现这些考生交卷
 * 3、对于120分钟内没有完成考试的考生，在120分钟考试时间到后需要让他们强制交卷
 * 4、在所有的考生都交完卷后，需要将控制线程关闭
 * <p/>
 * 实现思想：用DelayQueue存储考生（Student类），每一个考生都有自己的名字和完成试卷的时间，Teacher线程对DelayQueue进行监控，收取完成试卷小于120分钟的学生的试卷。当考试时间120分钟到时，先关闭Teacher线程，然后强制DelayQueue中还存在的考生交卷。
 * 每一个考生交卷都会进行一次countDownLatch.countDown()，当countDownLatch.await()不再阻塞说明所有考生都交完卷了，而后结束考试。
 *
 * @auther WEI.DUAN
 * @date 2017/5/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DelayQueueDemo1 {

    static final StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) throws InterruptedException {
        int studentNumber = 20;
        CountDownLatch countDownLatch = new CountDownLatch(studentNumber + 1);
        DelayQueue<Student> students = new DelayQueue<Student>();
        Random random = new Random();
        for (int i = 0; i < studentNumber; i++) {
            students.put(new Student("student" + (i + 1), 30 + random.nextInt(120), countDownLatch));
        }
        Thread teacherThread = new Thread(new Teacher(students));
        students.put(new EndExam(students, 120, countDownLatch, teacherThread));
        teacherThread.start();
        countDownLatch.await();
        System.out.println(" 考试时间到，全部交卷！");
    }

    static class Student implements Runnable, Delayed {
        private String name;
        private long workTime;
        private long submitTime;
        private boolean isForce = false;
        private CountDownLatch countDownLatch;

        public Student() {
        }

        public Student(String name, long workTime, CountDownLatch countDownLatch) {
            this.name = name;
            this.workTime = workTime;
            //在初始化对象的时候给过期时间赋值，比如参数workTime=30s 那么就是30秒后过期
            //这里要加上System.nanoTime()当前时间的纳秒，是因为在getDelay()中会一直不停的减去System.nanoTime()，直到为负值的时候就说明过期了
            this.submitTime = TimeUnit.NANOSECONDS.convert(workTime, TimeUnit.SECONDS) + System.nanoTime();
            this.countDownLatch = countDownLatch;
        }

        @Override
        public int compareTo(Delayed o) {
            if (o == null || !(o instanceof Student)) {return 1;}
            if (o == this) {return 0;}
            Student s = (Student) o;
            if (this.workTime > s.workTime) {
                return 1;
            } else if (this.workTime == s.workTime) {
                return 0;
            } else {
                return -1;
            }
        }

        //判断队列中的元素是否已到达延时时间 因为在构造函数中workTime+System.nanoTime()
        //所以在这里要减去System.nanoTime()
        // 结果 <= 0 代表已过期
        @Override
        public long getDelay(TimeUnit unit) {
//            return unit.convert(submitTime - System.nanoTime(), TimeUnit.NANOSECONDS);
            return submitTime - System.nanoTime();
        }

        @Override
        public void run() {
            if (isForce) {
                System.out.println(name + " 交卷, 希望用时" + workTime + "分钟" + " ,实际用时 120分钟");
            } else {
                System.out.println(name + " 交卷, 希望用时" + workTime + "分钟" + " ,实际用时 " + workTime + " 分钟");
            }
            countDownLatch.countDown();
        }

        public boolean isForce() {
            return isForce;
        }

        public void setForce(boolean isForce) {
            this.isForce = isForce;
        }

    }

    /**
     * 因为考试时间是120分钟，所以delayqueue中offer这个EndExam对象的延时(过期)也就是120分钟
     * 当这个对象的延时时间到了的时候，该对象被Teacher取出来执行
     * 运行run之后会取出剩下的student(workTime >= 120)
     */
    static class EndExam extends Student {

        private DelayQueue<Student> students;
        private CountDownLatch countDownLatch;
        private Thread teacherThread;

        public EndExam(DelayQueue<Student> students, long workTime, CountDownLatch countDownLatch, Thread teacherThread) {
            super("强制收卷", workTime, countDownLatch);
            this.students = students;
            this.countDownLatch = countDownLatch;
            this.teacherThread = teacherThread;
        }

        @Override
        public void run() {
            teacherThread.interrupt();
            Student tmpStudent;
            for (Iterator<Student> iterator2 = students.iterator(); iterator2.hasNext(); ) {
                tmpStudent = iterator2.next();
                tmpStudent.setForce(true);
                tmpStudent.run();
            }
            countDownLatch.countDown();
        }

    }

    static class Teacher implements Runnable {

        private DelayQueue<Student> students;

        public Teacher(DelayQueue<Student> students) {
            this.students = students;
        }

        @Override
        public void run() {
            try {
                System.out.println(" test start");
                while (!Thread.interrupted()) {
                    stopWatch.start();
                    //这里可以确认take()获取队列中元素的时间就是
                    Student student = students.take();
                    stopWatch.stop();
                    System.out.println(stopWatch.getTotalTimeSeconds());
                    student.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
