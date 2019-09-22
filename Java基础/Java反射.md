# 反射总结
反射就是通过获取类的字节码文件，也就是.class文件，动态获取对象信息和调用对象方法。
#1、获取Class的三种方式
```java 
//1、getClass()
Class class1 = Object.getClass();
//2、Class.class
Class class2 = Object.class;
//3、根据累的全路径获取
Class class3 = Class.forName("com.xxx.Test");
```
#2、通过反射获取类的构造方法、方法以及属性
```java 
Class class = Class.forName("com.xxx.Test");
//获取类构造器
//1、获取所有公用构造方法
Constructor[] constructor = class.getConstructors();
//2、获取所有的构造方法
Constructor[] declaredConstructor = class.getDeclaredConstructors();
//3、获取公用无参构造方法
Constructor constructor = class.getConstructor(null);
//4、获取公用有参构造方法
Constructor constructor = class.getConstructor(new Class[]{String.class,Integer.class});
//5、获取有参构造方法
Constructor declaredConstructor = class.getDeclaredConstructor(new Class[]{String.class,Integer.class});

//获取类属性
//1、获取所有公有属性
Field[] fields = class.getFields();
//2、获取所有属性(公有+私有)
Field[] declareFields = class.getDeclareFields();
//3、获取指定公有属性并使用
Field field = class.getFields("name");
Test test1 = class.getConstructor().newInstance();
//为属性设置值
field.set(test1,"111");
//4、获取指定私有属性并使用
Field declareField = class.getDeclareFields("type");
Test test3 = class.getConstructor().newInstance();
//暴力反射
declareField.setAccessble(true);
//为属性设置值
field.set(test2,"111");

//获取类方法
//1、获取所有public修饰的方法
Method[] methods = class.getMethods();
//2、获取所有方法
Method[] declareMethods = class.getDeclareMethods();
//3、获取public修饰的带参方法并使用
Method method = class.getMethod("method1",String.class);
//4、获取特定带参方法并使用
Method declareMethod = class.getDeclareMethod("method2",String.class,Integer.class,String.class);
//反射调用给方法传值
declareMethod.invoke(test1,"aa",11,"bb")

//获取注解 
...
```

