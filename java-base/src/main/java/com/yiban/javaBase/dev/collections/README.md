- Java提供了你可以在你的并发程序中使用的，而且不会有任何问题或不一致的数据集合。基本上，Java提供两种在并发应用程序中使用的集合：

- 阻塞集合：这种集合包括添加和删除数据的操作。如果操作不能立即进行，是因为集合已满或者为空，该程序将被阻塞，直到操作可以进行。
- 非阻塞集合：这种集合也包括添加和删除数据的操作。如果操作不能立即进行，这个操作将返回null值或抛出异常，但该线程将不会阻塞。


- 非阻塞列表，使用ConcurrentLinkedDeque类。
- 阻塞列表，使用LinkedBlockingDeque类。
- 用在生产者与消费者数据的阻塞列表，使用LinkedTransferQueue类。
- 使用优先级排序元素的阻塞列表，使用PriorityBlockingQueue类。
- 存储延迟元素的阻塞列表，使用DelayQueue类。
- 非阻塞可导航的map，使用ConcurrentSkipListMap类。
- 随机数，使用ThreadLocalRandom类
- 原子变量，使用AtomicLong和AtomicIntegerArray类