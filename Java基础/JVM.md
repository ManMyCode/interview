# 1、JVM虚拟机模型

# 2、类加载机制
- JVM虚拟机启动时根据内存配置要求，为JVM申请特定大小的内存空间(JVM内存按照功能划分，可以粗略地划分为方法区(Method Area) 
和堆(Heap),而所有的类的定义信息都会被加载到方法区中。)
- 内存空间申请好后，JVM会创建一个使用C++语言实现的引导类加载器(Bootstrap Classloader)实例，负责加载JVM虚拟机运行时所需的系统级别的基本类，即{JRE_HOME}/lib目录下的jar包和配置到内存。
- 创建JVM启动器实例Launcher,并获取类加载器。
    - Launcher的内部，其定义了两个类加载器(ClassLoader),分别是拓展类加载器(ExtClassLoader) 和 应用类加载器(AppClassLoader)。
    - ExtClassLoader和AppClassLoader均继承自共同的父类(抽象类ClassLoader的子类)URLClassLoader,从ClassLoader的源码可以看出，双亲委派模型的具体实现
    - 方法执行完毕后，JVM销毁，释放内存
```java
    /**
     * #loadClass(String, boolean)} method.  It is invoked by the Java virtual
     * machine to resolve class references. 
     */
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // 首先，检查是否已经被当前的类加载器记载过了，如果已经被加载，直接返回对应的Class<T>实例
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        //如果有父类加载器，则先让父类加载器加载
                        c = parent.loadClass(name, false);
                    } else {
                        // 没有父加载器，则查看是否已经被引导类加载器加载，有则直接返回
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }
                // 父加载器加载失败，并且没有被引导类加载器加载，则尝试该类加载器自己尝试加载
                if (c == null) {
                    // 尝试自己加载
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```
# 3、双亲委派模型
- 委托父类加载器ExtClassLoader帮忙加载({JRE_HOME}/lib/ext/目录下)；
- 父类加载器ExtClassLoader加载不了，则查询引导类加载器Bootstrap Class Loader有没有加载过该类；
- 如果引导类加载器Bootstrap Class Loader没有加载过该类，则当前的类加载器AppClassLoader应该自己加载该类；
- 若加载成功，返回 对应的Class<T> 对象；若失败，抛出异常“ClassNotFoundException”。
    
