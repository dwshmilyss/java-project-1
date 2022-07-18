Netty以及其他FastUtils之类的原始类型map，都支持key是int或 long。但两者的区别并不仅仅在于int 换 Integer的那点空间，而是整个存储结构和Hash冲突的解决方法都不一样。

HashMap的结构是 Node[] table; Node 下面有Hash，Key，Value，Next四个属性。
而IntObjectHashMap的结构是int[] keys 和 Object[] values.

在插入时，同样把int先取模落桶，如果遇到冲突，则不采样HashMap的链地址法，而是用开放地址法（线性探测法）index＋1找下一个空桶，最后在keys[index]，values[index]中分别记录。在查找时也是先落桶，然后在key[index++]中逐个比较key。

所以，对比整个数据结构，省的不止是int vs Integer，还有每个Node的内容。
而性能嘛，IntObjectHashMap还是稳赢一点的，随便测了几种场景，耗时至少都有24ms vs 28ms的样子，好的时候甚至快1/3。



### 优化建议
1. 考虑加载因子地设定初始大小
2. 减小加载因子
3. String类型的key，不能用==判断或者可能有哈希冲突时，尽量减少长度
4. 使用定制版的EnumMap
5. 使用IntObjectHashMap