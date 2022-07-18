##CAS 和 Unsafe
CAS即 compare and swap，设计并发时的一种常用技术，他拥有volatile读和volatile写双重内存原语
java.util.concurrent包全完建立在CAS之上，没有CAS也就没有此包，可见CAS的重要性。

当前的处理器基本都支持CAS，只不过不同的厂家的实现不一样罢了。CAS有三个操作数：**内存值V**、**旧的预期值A**、**要修改的值B**，当且仅当预期值A和内存值V相同时，将内存值修改为B并返回true，否则什么都不做并返回false。

CAS也是通过Unsafe实现的，看下Unsafe下的三个方法：
1. public final native boolean compareAndSwapObject(Object paramObject1, long paramLong, Object paramObject2, Object paramObject3);

2. public final native boolean compareAndSwapInt(Object paramObject, long paramLong, int paramInt1, int paramInt2);

3. public final native boolean compareAndSwapLong(Object paramObject, long paramLong1, long paramLong2, long paramLong3);


CAS也有一个缺点 即ABA问题(即值初始化是A，然后被修改成B，接着又被修改成A，这时候CAS认为该值没有被修改过)，可以使用加版本号的方式解决该问题。java.util.concurrent提供了一个带有标记的原子引用类”AtomicStampedReference”，它可以通过控制变量值的版本来保证CAS的正确性。