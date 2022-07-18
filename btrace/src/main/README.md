### 可以在不改变源码的情况下监控方法的执行情况

#### 运行指南
1. 启动要监控的java进程
2. jps找到该进程的pid
3. 写一个btrace监控的java类 TraceDemo1
4. btrace $pid TraceDemo1.java
---
#### 注意
- location=@Location(Kind.RETURN)//函数返回的时候执行，如果不填，则在函数开始的时候执行
- btrace注解的java类不能带有中文字符，中文注释也不行
- btrace注解的java类不能调用一些java本身的方法（比如在输出时非字符串类型转字符串，不能用String.valueOf，而要用btrace自身提供的str()函数。原因是btrace存在安全限制，不允许调用被agent对象的方法。网上说可以通过设计Dcom.sun.btrace.unsafe=true破解btrace的安全限制，但我试过不行。）