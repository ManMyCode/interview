# 泛型中的通配符
## 1、常用通配符T，E，K，V，？
本质上这些个都是通配符，没啥区别
- ？表示不确定的 java 类型
- T (type) 表示具体的一个java类型
- K V (key value) 分别代表java键值中的Key Value
- E (element) 代表Element

## 2、上界通配符 < ? extends E>
用 extends 关键字声明，表示参数化的类型可能是所指定的类型，或者是此类型的子类。
- 如果传入的类型不是 E 或者 E 的子类，编译不成功
- 泛型中可以使用 E 的方法，要不然还得强转成 E 才能使用
- 类型参数列表中如果有多个类型参数上限，用逗号分开
```java
private <K extends A, E extends B> E test(K arg1, E arg2){
    E result = arg2;
    arg2.compareTo(arg1);
    //...
    return result;
}
```
## 3、下界通配符 < ? super E>
用 super 进行声明，表示参数化的类型可能是所指定的类型，或者是此类型的父类型，直至 Object

## 4、？和 T 的区别
？和 T 都表示不确定的类型，区别在于我们可以对 T 进行操作，但是对 ？不行，比如如下这种 ：
```java
// 可以
T t = operate();
// 不可以
？car = operate();
```

[参考地址](https://mp.weixin.qq.com/s/jZaTyBmzXNm1knDWsP3eJA)