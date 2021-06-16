# Spring 高频问题整理
## 1.IOC(控制反转)：

- IOC(Inversion Of Controll)是一种设计思想，将原本在程序中手动创建对象的控制权，交由给Spring框架来管理,管理对象（通过依赖注入（DI），装配对象，配置对象，并且管理这些对象的整个生命周期。IOC容器是Spring用来实现IOC的载体，IOC容器实际上就是一个Map(key, value)，Map中存放的是各种对象。这样可以很大程度上简化应用的开发，把应用从复杂的依赖关系中解放出来。IOC容器就像是一个工厂，当需要创建一个对象，只需要配置好配置文件/注解即可，不用考虑对象是如何被创建出来的，大大增加了项目的可维护性且降低了开发难度。
- 实现原理就是工厂模式加反射机制。IOC主要实现方式有两种：依赖注入和依赖查找。
  - 依赖查找是通过资源定位，把对应的资源查找回来
  - DI(依赖注入)：(Dependency Injection)即组件之间的依赖关系由容器在应用系统运行期来决定，也就是由容器动态地将某种依赖关系的目标对象实例注入到应用系统中的各个关联的组件之中。组件不做定位查询，只提供普通的Java方法让容器去决定依赖关系。
    - 1.构造器依赖注入：构造器依赖注入通过容器触发一个类的构造器来实现的，该类有一系列参数，每个参数代表一个对其他类的依赖。
      - ```xml 
          <bean id="user" class="com.xx.User">
              <constructor-arg index="0" value="张三"/>
              <consor-artructg index="1" value="男"/>
          </bean>```
          ```
  
- 2.Setter方法注入：Setter方法注入是容器通过反射调用无参构造器或无参static工厂 方法实例化bean之后，调用该bean的setter方法，即实现了基于setter的依赖注入。
  
      - ```xml
        <bean id="user" class="com.xx.User">
            <property name="userName" value="张三"/>
            <property name="sex" value="男"/>
        </bean>```
        ```
  
    - 接口注入：比如数据库连接资源完全可以在Tomcat下配置，然后通过JNDI的形式去获取它，这样数据库连接资源是属于开发工程外的资源，这个时候我们可以采用接口注入的形式来获取它 

## 2.AOP：

- AOP（Aspect-Oriented Programming，面向切面编程）能够将那些与业务无关，却为业务模块所共同调用的逻辑或责任（例如事务处理、日志管理、权限控制等）封装起来，便于减少系统的重复代码，降低模块间的耦合度，并有利于未来的可扩展性和可维护性。使用AOP之后我们可以把一些通用功能抽象出来，在需要用到的地方直接使用即可，这样可以大大简化代码量，提高了系统的扩展性。Spring AOP是基于动态代理的，如果要代理的对象实现了某个接口，那么Spring AOP就会使用JDK动态代理去创建代理对象；而对于没有实现接口的对象，就无法使用JDK动态代理，转而使用CGlib动态代理生成一个被代理对象的子类来作为代理。

## 3.Spring AOP / AspectJ AOP 的区别？

- Spring AOP属于运行时增强，而AspectJ是编译时增强。
- Spring AOP基于代理（Proxying），而AspectJ基于字节码操作（Bytecode Manipulation）。
- AspectJ相比于Spring AOP功能更加强大，但是Spring AOP相对来说更简单。如果切面比较少，那么两者性能差异不大。但是，当切面太多的话，最好选择AspectJ，它比SpringAOP快很多。

## 4.Bean生命周期：

- ![Bean生命周期](/主流框架/images/bean生命周期.png)
- `1`.`实例化`；
- `2`.将值和引用注入到对应的属性中(`填充属性`)；
- `3`.如果bean实现了BeanNameAware接口，则调用`setBeanName()`方法；
- `4`.如果bean实现了BeanFactoryAware接口，调用`setBeanFactory()`方法，传入BeanFactory容器实例；
- `5`.如果bean实现了ApplicationContextAware接口，调用`setApplicationContext()`方法，将bean所在的应用上下文的引用传入进来；
- `6`.如果bean实现了BeanPostProcessor接口，调用它们的`post-ProcessBeforeInitialization()`方法；
- `7`.如果bean实现了InitializingBean接口，调用它们的`after-PropertiesSet()`方法。类似地，如果bean使用initmethod声明了初始化方法，该方法也会被调用；
- `8`.调用它们的after-PropertiesSet()方法。类似地，如果bean使用`initmethod`声明了初始化方法，该方法也会被调用；
- `9`.如果bean实现了BeanPostProcessor接口，调用它们的`post-ProcessAfterInitialization()`方法；
- `10`.`bean已准备就绪`，并一直驻留在应用上下文，知道应用上下文被销毁；
- `11`.如果bean实现了DisposableBean接口，调用它的`destroy()`接口方法。如果bean使用destroy-method声明了销毁方法，该方法也会被调用；

## 5.spring 自动装配 bean 有哪些方式

- 1.`no`：默认的方式是不进行自动装配的，通过手工设置ref属性来进行装配bean;
- 2.`byName`：通过bean的名称进行自动装配，如果一个bean的 property 与另一bean 的name 相同，就进行自动装配;
- 3.`byType`：通过参数的数据类型进行自动装配;
- 4.`constructor`：利用构造函数进行装配，并且构造函数的参数通过byType进行装配;
- 5.`autodetect`：自动探测，如果有构造方法，通过 construct的方式自动装配，否则使用 byType的方式自动装配;

## 6.Spring 注解

- 开启注解装配：在Spring配置文件中配置 ```<context:annotation-config/>```元素；
- `@Component`：这将 java 类标记为 bean。它是任何 Spring 管理组件的通用构造型。spring 的组件扫描机制现在可以将其拾取并将其拉入应用程序环境中；
- `@Controller`：这将一个类标记为 Spring Web MVC 控制器。标有它的 Bean 会自动导入到 IoC 容器中；
- `@Service`：此注解是组件注解的特化。它不会对 @Component 注解提供任何其他行为。您可以在服务层类中使用 @Service 而不是 @Component，因为它以更好的方式指定了意图；
- `@Repository`：这个注解是具有类似用途和功能的 @Component 注解的特化。它为 DAO 提供了额外的好处。它将 DAO 导入 IoC 容器，并使未经检查的异常有资格转换为 Spring DataAccessException；
- `@Required`：表明bean的属性必须在配置的时候设置，通过一个bean定义的显式的属性值或通过自动装配，若@Required注解的bean属性未被设置，容器将抛出BeanInitializationException;
- `@Autowired`：默认是按照类型装配注入的，默认情况下它要求依赖对象必须存在（可以设置它required属性为false）。@Autowired 注解提供了更细粒度的控制，包括在何处以及如何完成自动装配。可用于：构造函数、成员变量、Setter方法；
- `@Resource`：默认是按照名称来装配注入的，只有当找不到与名称匹配的bean才会按照类型来装配注入；
- `Qualifier`：当您创建多个相同类型的 bean 并希望仅使用属性装配其中一个 bean 时，您可以使用@Qualifier 注解和 @Autowired 通过指定应该装配哪个确切的 bean 来消除歧义；
- `@RequestMapping`：注解用于将特定 HTTP 请求方法映射到将处理相应请求的控制器中的特定类/方法。
  - 类级别：映射请求的 URL
  - 方法级别：映射 URL 以及 HTTP 请求方法

## 7.Spring如何处理线程并发问题

只有无状态的Bean才可以在多线程环境下共享，在Spring中，绝大部分Bean都可以声明为singleton作用域，因为Spring对一些Bean中非线程安全状态采用ThreadLocal进行处理，解决线程安全问题。

## 8.Spring事务隔离级别和传播行为

- 脏读：一个事务正在访问数据，并对数据做了修改，但没有提交到数据库中，这是另外一个事务也访问了这个数据，然后使用了这个数据。
- 幻读：指事务不是独立执行时发生的一种现象，比如第一个事务对一个表中的数据进行了修改，这种修改涉及到表中的全部数据行。同事，第二个事务也修改这个表中的数据，这种修改是向表中插入一行新数据。那么，以后就会发生操作第一个事务的用户发现表中还有没有修改的数据行，就好像发生了幻觉一样。
- 不可重复读：在一个事务内，多次读同一数据，在这个事务还没有结束时，另外一个事务也访问该同一数据。那么，在第一个事务中的两次读数据之间，由于第二个事务的修改，那么第一个事务两次督导的数据可能是不一样的。

### 8.1 五个事务隔离级别

- `ISOLATION_DEFAULT `:默认隔离级别,使用数据库设置的隔离级别。
- `ISOLATION_READ_UNCOMMITTED `:最低的隔离级别，允许其他事务看到这个事务未提交的数据，会产生脏读，幻读，不可重复读。
- `ISOLATION_READ_COMMITTED `:保证一个事物修改的数据提交后才能被另外一个事务读取，可以避免脏读，但可能会出现不可重复读和幻读
- `ISOLATION_REPEATABLE_READ `:可以防止脏读，不可重复读，但可能出现幻读。
- `ISOLATION_SERIALIZABLE `:花费最高代价，但最可靠的事务隔离级别，食物被处理为顺序执行，避免了脏读，幻读和重复读。

### 8.2 七个事务传播行为

- `PROPAGATION_REQUIRED `:如果存在一个事务，则支持当前事务，如果没有事务则开启一个新事务。
- `PROPAGATION_SUPPORTS `:如果存在一个事务，则支持当前事务，如果没有事务，则非事务的执行，但对于事务同步的事务管理器，PROPAGATION_SUPPORTS与不使用事务有少许不同。
- `PROPAGATION_MANDATORY `:如果存在一个事务，则支持当前事务，如果没有一个活动的事务，则抛出异常。
- `PROPAGATION_REQUIRES_NEW `:总是开启一个新的事务，如果一个事务已经存在，则将这个存在的事务挂起。
- `PROPAGATION_NOT_SUPPORTED `:总是非事务的执行，并挂起任何存在的事务。
- `PROPAGATION_NEVER `:总是非事务的执行 ，如果存在一个活动的事务，则抛出异常。
- `PROPAGATION_NESTED`:如果一个活动的事务存在，则运行在一个嵌套的事务中，如果没有活动事务，则按TransactionDefinition.PROPAGATION_REQUIRED属性执行。

## 9.Spring循环依赖

Spring内部DefaultSingletonBeanRegistry类中维护了三个Map，也就是我们通常说的三级缓存。

- 一级缓存：singletonObjects ，俗称“单例缓存池”，缓存创建完成单例Bean的地方。
- 二期缓存：earlySingletonObjects 映射Bean的早期引用，也就是说在这个Map里的Bean不是完整的，甚至还不能称之为“Bean”，只是一个Instance。
- 三级缓存：singletonFactories 映射创建Bean的原始工厂。
