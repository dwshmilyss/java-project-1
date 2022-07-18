##### CountDownLatch 
```aidl
当一个或多个线程调用await方法时，这些线程会阻塞。其它线程调用countDown方法会将计数器减1，当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行。
实现原理：计数器的值由构造函数传入，并用它初始化AQS的state值。当线程调用await方法时会检查state的值是否为0，如果是就直接返回（即不会阻塞）；如果不是，将表示该节点的线程入列，然后将自身阻塞。当其它线程调用countDown方法会将计数器减1，
然后判断计数器的值是否为0，当它为0时，会唤醒队列中的第一个节点，由于CountDownLatch使用了AQS的共享模式，所以第一个节点被唤醒后又会唤醒第二个节点，以此类推，使得所有因await方法阻塞的线程都能被唤醒而继续执行。
```
---
##### CyclicBarrier
```aidl
CyclicBarrier和CountDownLatch都可以实现线程的等待，其区别主要有两点：
(01) CountDownLatch的作用是允许1或N个线程等待其他线程完成执行；而CyclicBarrier则是允许N个线程相互等待。
(02) CountDownLatch的计数器无法被重置；CyclicBarrier的计数器可以被重置后使用，因此它被称为是循环的barrier。

CyclicBarrierd的函数列表：
CyclicBarrier(int parties)
创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，但它不会在启动 barrier 时执行预定义的操作。
CyclicBarrier(int parties, Runnable barrierAction)
创建一个新的 CyclicBarrier，它将在给定数量的参与者（线程）处于等待状态时启动，并在启动 barrier 时执行给定的屏障操作，该操作由最后一个进入 barrier 的线程执行。
int await()
在所有参与者都已经在此 barrier 上调用 await 方法之前，将一直等待。
int await(long timeout, TimeUnit unit)
在所有参与者都已经在此屏障上调用 await 方法之前将一直等待,或者超出了指定的等待时间。
int getNumberWaiting()
返回当前在屏障处等待的参与者数目。
int getParties()
返回要求启动此 barrier 的参与者数目。
boolean isBroken()
查询此屏障是否处于损坏状态。
void reset()
将屏障重置为其初始状态。

```
