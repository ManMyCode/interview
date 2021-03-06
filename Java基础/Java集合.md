# 常用集合问题整理
## 一、` ArrayList,LinkedList,HashSet，LinkedHashSet,TreeSet`   

  * `ArrayList`：有序集合、插入，删除数据慢(O(N)),查找快(O(1))，类似于数组。
  * `LinkedList`: 链表结构、插入，删除数据块(O(1)),查找慢(O(N)).
  * `HashSet`: 无序集合，元素唯一，添加元素时内部调用了HashMap的put方法，HashMap的key即为添加进去的值。
  * `LinkedHashSet`:数据按照插入顺序存放。
  * `TreeSet`:从小到大顺序排序
  * `list`:可以插入多个null元素

## 二、`HashMap,HashTable,LinkedHashMap,TreeMap`   

1.`HashMap`:非线程安全，使用`Collections.synchronizedList(list) `转换成线程成全的容器,jdk1.8之前由数组+链表组成，jdk1.8以后当链表长度大于阈值默认为8时，将链表转化为红黑树，以减少搜索时间。  

  * 构造HashMap时的两个默认参数：initialCapacity初始容量（默认16）、loadFactor装载因子（默认0.75）
  * 向容器中添加元素的时候，如果元素个数达到阈值（元素个数=数组长度*loadFactor），就要自动扩容。
  * 使用一个新的数组代替已有的数组，每次扩容为先前的两倍。
  
2.`HashTable`:线程安全,Entry数组 + 链表的方法实现  

  * public函数几乎都是同步的（synchronized关键字修饰）
  * HashTabale初始的容量为11，负载因子为0.75
  * 让HashMap同步：```Map map = Collections.synchronizedMap(hashMap);```
  
3.`LinkedHashMap`:在HashMap的基础上多了一个双向链表来维持保持插入顺序  

4.`TreeMap`:数据结构是红黑树。  

## 三、集合修改

1.设置为只读

```java
Collection<String> clist = Collections. unmodifiableCollection(list);
List<String> rlist = Collections. unmodifiableList(list);
Map<String,Object> rMap = Collections. unmodifiableMap(map);
```
2.边遍历边移除集合的元素

```java
Iterator<Integer> it = list.iterator();
while(it.hasNext()){
   *// do something*
   it.remove();
}
```
3.多线程访问集合避免出现ConcurrentModificationException 异常

  * 在遍历过程中，所有涉及到改变modCount值得地方全部加上synchronized。
  * 使用CopyOnWriteArrayList来替换ArrayList
