# Hibernate-Test2(c3p0)
1. 在 hibernate 中使用 C3P0 数据源:

1). 导入 jar 包:

hibernate-release-4.2.4.Final\lib\optional\c3p0\*.jar

2). 加入配置:
  
hibernate.c3p0.max_size: 数据库连接池的最大连接数  
hibernate.c3p0.min_size: 数据库连接池的最小连接数  
hibernate.c3p0.acquire_increment: 当数据库连接池中的连接耗尽时, 同一时刻获取多少个数据库连接  

hibernate.c3p0.timeout:   数据库连接池中连接对象在多长时间没有使用过后，就应该被销毁  
hibernate.c3p0.idle_test_period:  表示连接池检测线程多长时间检测一次池内的所有链接对象是否超时.   
连接池本身不会把自己从连接池中移除，而是专门有一个线程按照一定的时间间隔来做这件事，  
这个线程通过比较连接对象最后一次被使用时间和当前时间的时间差来和 timeout 做对比，进而决定是否销毁这个连接对象。   

hibernate.c3p0.max_statements:  缓存 Statement 对象的数量

(only for Oracle, not for mysql){  
设定 JDBC 的 Statement 读取数据的时候每次从数据库中取出的记录条数  
property name="hibernate.jdbc.fetch_size">100</property  
    	  
设定对数据库进行批量删除，批量更新和批量插入的时候的批次大小  
}  
property name="jdbc.batch_size">30</property  
