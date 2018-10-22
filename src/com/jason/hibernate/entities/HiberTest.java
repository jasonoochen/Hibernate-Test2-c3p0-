package com.jason.hibernate.entities;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HiberTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void init() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
											.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
	}
	
	@After
	public void destory() {
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	
	@Test
	public void testDoWork(){
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				System.out.println(connection); 
			}
		});
	}
	
	/*
	 * session.evict() 从session缓存中把指定的持久化对象移除
	 */
	@Test
	public void testEvict(){
		News news1 = (News) session.get(News.class, 1);
		News news2 = (News) session.get(News.class, 2);
		
		news1.setTitle("AA");
		news2.setTitle("BB");
		
		session.evict(news1); 
	}
	/*
	 * session.delete()
	 * 执行删除操作，只要OID和数据表中一条记录对应，就会执行delete操作
	 * 若OID在数据表中没有对应的记录，抛出异常
	 * 
	 * 可以通过设置hibernate配置文件hibernate.use_identifier_rollback为true
	 * 使删除对象后，把其OID设为null
	 */
	public void testDelete(){
//		News news = new News();
//		news.setId(11);
		
		News news = (News) session.get(News.class, 163840);
		session.delete(news); 
		
		System.out.println(news);
	}
	/*
	 * session.saveOrUpdate();
	 * 按OID来判断，若对象的OID为null，进行save操作，若对象的OID有值，进行update操作
	 * 1.若OID不为null,但数据表中还没有和其对应的记录，会抛出异常
	 * 2.了解：OID值等于ID的unsaved-value属性值的对象，也会被认为是一个游离对象
	 */
	
	@Test
	public void testSaveOrUpdate(){
		News news = new News("FFF", "fff", new Date());
		news.setId(11);
		
		session.saveOrUpdate(news); 
	}
	
	/*
	 * session.update()
	 * 1.若更新一个持久化对象，不需要显示的调用update方法，因为在调用transaction
	 *   的commit()方法时，会先执行session的flush方法。
	 * 2.更新一个游离对象，需要显式的调用session的update方法。
	 *   可以把一个游离对象变为持久化对象。(重新放入session中)
	 * 
	 * 注意：
	 * 1.无论要更新的游离对象和数据表的记录是否一致，都会发送update语句。
	 *   如何让update方法不再盲目的发出update语句？
	 *   在.hbm.xml 文件的class节点设置 select-before-update = "true"(默认为false)。
	 *   通常不需要设置该属性(会多发送select语句，降低效率（一般情况下）)
	 * 2.若数据表中没有对应的记录，但还是调用了update方法，会抛出异常
	 * 3.当update()方法关联一个游离对象时，如果在session的缓存中已经存在相同OID的持久化对象，会抛出异常
	 *   在session缓存中，不能有两个OID相同的对象。
	 */
	@Test
	public void testUpdate(){
		News news = (News) session.get(News.class, 1);
		
		transaction.commit();
		session.close();
		
//		news.setId(100);

		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
//		news.setAuthor("SUN"); //news是一个游离对象
		
		News news2 = (News) session.get(News.class, 1);
		session.update(news);
		//session缓存中有两个OID相同的对象，抛出异常
	}
	
	/*
	 * 1.session.save()
	 * 1).使一个临时对象变为持久化对象
	 * 2).为对象分配ID
	 * 3).在flush缓存时会发送一条insert语句
	 * 4).在save方法之前的id是无效的
	 * 5).持久化对象的ID是不能被修改的
	 */
	/*
	 * persist():也会执行insert操作
	 * 和save()的区别：
	 * 在调用persist方法之前，若对象已经有ID了，就不会执行insert,而是抛出异常
	 */
	/*
	 * session.get()
	 * session.load()
	 * 1.执行get()会立即加载对象（进行数据库查询，从数据库获取对象）（立即检索）
	 *   执行load方法，若不适用该对象，则不执行查询操作，而返回一个代理对象（延迟检索）
	 *   （不立即进行数据库查询，直到需要用了才加载对象）
	 * 2.若对数据表中没有的对应的记录，且session也没有被关闭，同时需要使用对象时：
	 *   get: 返回null
	 *   load: 若不适用该对象的任何属性，没问题；若需要初始化了，抛出异常
	 * 3.load方法可能会抛出LazyInitializationException异常
	 *   在需要初始化代理对象之前已经关闭了session,抛出懒加载异常
	 */
	
	@Test
	public void testLoad(){
		
		News news = (News) session.load(News.class, 10);
		System.out.println(news.getClass().getName()); 
		
//		session.close();
//		System.out.println(news); 
	}
	@Test
	public void testGet(){
		News news = (News) session.get(News.class, 1);
//		session.close();
		System.out.println(news); 
	}
	@Test
	public void testPersist(){
		News news = new News();
		news.setTitle("EE");
		news.setAuthor("ee");
		news.setDate(new Date());
		news.setId(200); 
		
		session.persist(news); 
	}
	
	@Test
	public void testSave(){
		News news = new News();
		news.setTitle("CC");
		news.setAuthor("cc");
		news.setDate(new Date());
		news.setId(100); //在save()之前setID无效
		
		System.out.println(news);		
		session.save(news);
		System.out.println(news);
//		news.setId(101); //持久化对象的ID是不能被修改的
	}
	
	@Test
	public void testClear() {
		News news1 = (News) session.get(News.class, 1);
		session.clear();
		News news2 = (News) session.get(News.class, 1);
	}
	
	/*
	 * connection.isolation
	 * 事务隔离级别 - 
	 */
	/*
	 * session.clear()
	 * 清理缓存
	 */
	/*
	 * session.refresh()
	 * refresh():会强制发送select语句，使session缓存中对象的状态和数据表中的状态一样。
	 */
	
	/*
	 * session.flush()
	 * flush:使数据表中的记录和Session缓存中的对象的状态保持一致，为了保持一致，可能会发送对应的SQL语句。
	 * 1.在Transaction的commit()方法中：先调用session的flush方法，再提交事务
	 * 2.flush()方法可能会发送SQL语句，但不会提交事务
	 * 3.注意：在未提交事务或显式的调用session.flush()方法之前，也有可能会进行flush()操作。
	 * 1).执行HQL或QBC查询，会先进行flush()操作，以得到数据表的最新的记录。
	 * 2).若记录的ID是由底层数据库使用自增的方式生成的，则在调用save()方法后，就会立即发送insert语句
	 * 因为save方法后，必须保证对象的ID是存在的。
	 */
	
	@Test
	public void testSessionFlush() {
		News news = (News) session.get(News.class, 1);
		news.setAuthor("Oracle");
	}
	
	@Test
	public void test() {
		News news = (News) session.get(News.class, 1);
	}

}
