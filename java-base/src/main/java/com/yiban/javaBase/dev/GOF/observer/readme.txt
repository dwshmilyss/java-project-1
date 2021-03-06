抽象主题（Subject）：它把所有观察者对象的引用保存到一个聚集里，每个主题都可以有任何数量的观察者。抽象主题提供一个接口，可以增加和删除观察者对象。

具体主题（ConcreteSubject）：将有关状态存入具体观察者对象；在具体主题内部状态改变时，给所有登记过的观察者发出通知。

抽象观察者（Observer）：为所有的具体观察者定义一个接口，在得到主题通知时更新自己。

具体观察者（ConcreteObserver）：实现抽象观察者角色所要求的更新接口，以便使本身的状态与主题状态协调。

1.Subject 和 Observer 是一个一对多的关系，也就是说观察者只要实现 Observer 接口并把自己注册到 Subject 中就能够接收到消息事件；
2.Java API有内置的观察者模式类：Java.util.Observable 类和 java.util.Observer 接口，这分别对应着 Subject 和 Observer 的角色；
3.使用 Java API 的观察者模式类，需要注意的是被观察者在调用 notifyObservers() 函数通知观察者之前一定要调用 setChanged() 函数，要不然观察者无法接到通知；
4.使用 Java API 的缺点也很明显，由于 Observable 是一个类，java 只允许单继承的缺点就导致你如果同时想要获取另一个父类的属性时，你只能选择适配器模式或者是内部类的方式，而且由于 setChanged() 函数为 protected 属性，所以你除非继承 Observable 类，否则你根本无法使用该类的属性，这也违背了设计模式的原则：多用组合，少用继承。



总结
1）主题要知道哪些观察者对其进行监测，说明主题类中一定有一个集合类成员变量，添加和删除及判断这些观察者对象是否存在。

2）观察者类一定是多态的，有共同的父类接口。

3）主题完成的功能基本是固定的，添加观察者、撤销观察者、通知消息给观察者及引起观察者响应（即“拉”数据），可以抽象出来。