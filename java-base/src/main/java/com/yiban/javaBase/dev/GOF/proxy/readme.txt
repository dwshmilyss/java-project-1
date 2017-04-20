代理模式：
1、代理模式是常用的结构型设计模式之一，当无法直接访问某个对象或访问某个对象存在困难时可以通过一个代理对象来间接访问，为了保证客户端使用的透明性，所访问的真实对象与代理对象需要实现相同的接口。
2、主要由四个组件构成
    1/ Subject：抽象主题类，声明真实主题与代理的共同接口方法。
    2/ RealSubject：真实主题类，定义了代理所表示的真实对象，客户端通过代理类间接的调用真实主题类的方法。
    3/ Proxy：代理类，持有对真实主题类的引用，在其所实现的接口方法中调用真实主题类中相应的接口方法执行。
    4/ Client：客户端类。

3、静态代理
静态代理一般具有如下特点：
1.代理类一般要持有一个被代理的对象的引用
2.对于我们不关心的方法，全部委托给被代理
3.自己处理我们关心的方法

归结为一句话：代理类和被代理类实现相同的接口，代理类持有被代理类的引用，代理方法执行时，同步调用被代理类的相同方法。

4、动态代理
在运行时创建代理对象。主要用来做方法的增强，让你可以在不修改源码的情况下，增强一些方法，在方法执行前后做任何你想做的事情（甚至根本不去执行这个方法），
因为在InvocationHandler的invoke方法中，你可以直接获取正在调用方法对应的Method对象，具体应用的话，比如可以添加调用日志，做事务控制等

一个典型的动态代理创建对象过程可分为以下四个步骤：
1、通过实现InvocationHandler接口创建自己的调用处理器 IvocationHandler handler = new InvocationHandlerImpl(...);
2、通过为Proxy类指定ClassLoader对象和一组interface创建动态代理类
Class clazz = Proxy.getProxyClass(classLoader,new Class[]{...});
3、通过反射机制获取动态代理类的构造函数，其参数类型是调用处理器接口类型
Constructor constructor = clazz.getConstructor(new Class[]{InvocationHandler.class});
4、通过构造函数创建代理类实例，此时需将调用处理器对象作为参数被传入
Interface Proxy = (Interface)constructor.newInstance(new Object[] (handler));
为了简化对象创建过程，Proxy类中的newInstance方法封装了2~4，只需两步即可完成代理对象的创建。