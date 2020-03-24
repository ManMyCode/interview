# 常用集合问题整理
一、` ArrayList,LinkedList,HashSet，LinkedHashSet,TreeSet`   

  1.`ArrayList`：有序集合、插入，删除数据慢(O(N)),查找快(O(1))，类似于数组。
  2.`LinkedList`: 链表结构、插入，删除数据块(O(1)),查找慢(O(N)).
  3.`HashSet`: 无序集合，元素唯一。
  4.`LinkedHashSet`:数据按照插入顺序存放。
  5.`TreeSet`:从小到大顺序排序

二、`HashMap,HashTable,LinkedHashMap,TreeMap`   

1.`HashMap`:非线程安全,jdk1.8之前由数组+链表组成，jdk1.8以后当链表长度大于阈值默认为8时，将链表转化为红黑树，以减少搜索时间。  

  * 构造HashMap时的两个默认参数：initialCapacity初始容量（默认16）、loadFactor装载因子（默认0.75）
  * 向容器中添加元素的时候，如果元素个数达到阈值（元素个数=数组长度*loadFactor），就要自动扩容。
  * 使用一个新的数组代替已有的数组，每次扩容为先前的两倍。
  
2.`HashTable`:线程安全,Entry数组 + 链表的方法实现  

  * public函数几乎都是同步的（synchronized关键字修饰）
  * HashTabale初始的容量为11，负载因子为0.75
  * 让HashMap同步：```Map map = Collections.synchronizedMap(hashMap);```
  
3.`LinkedHashMap`:在HashMap的基础上多了一个双向链表来维持保持插入顺序  

4.`TreeMap`:数据结构是红黑树。  
