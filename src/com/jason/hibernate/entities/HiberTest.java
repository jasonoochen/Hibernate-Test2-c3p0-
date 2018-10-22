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
	 * session.evict() ��session�����а�ָ���ĳ־û������Ƴ�
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
	 * ִ��ɾ��������ֻҪOID�����ݱ���һ����¼��Ӧ���ͻ�ִ��delete����
	 * ��OID�����ݱ���û�ж�Ӧ�ļ�¼���׳��쳣
	 * 
	 * ����ͨ������hibernate�����ļ�hibernate.use_identifier_rollbackΪtrue
	 * ʹɾ������󣬰���OID��Ϊnull
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
	 * ��OID���жϣ��������OIDΪnull������save�������������OID��ֵ������update����
	 * 1.��OID��Ϊnull,�����ݱ��л�û�к����Ӧ�ļ�¼�����׳��쳣
	 * 2.�˽⣺OIDֵ����ID��unsaved-value����ֵ�Ķ���Ҳ�ᱻ��Ϊ��һ���������
	 */
	
	@Test
	public void testSaveOrUpdate(){
		News news = new News("FFF", "fff", new Date());
		news.setId(11);
		
		session.saveOrUpdate(news); 
	}
	
	/*
	 * session.update()
	 * 1.������һ���־û����󣬲���Ҫ��ʾ�ĵ���update��������Ϊ�ڵ���transaction
	 *   ��commit()����ʱ������ִ��session��flush������
	 * 2.����һ�����������Ҫ��ʽ�ĵ���session��update������
	 *   ���԰�һ����������Ϊ�־û�����(���·���session��)
	 * 
	 * ע�⣺
	 * 1.����Ҫ���µ������������ݱ�ļ�¼�Ƿ�һ�£����ᷢ��update��䡣
	 *   �����update��������äĿ�ķ���update��䣿
	 *   ��.hbm.xml �ļ���class�ڵ����� select-before-update = "true"(Ĭ��Ϊfalse)��
	 *   ͨ������Ҫ���ø�����(��෢��select��䣬����Ч�ʣ�һ������£�)
	 * 2.�����ݱ���û�ж�Ӧ�ļ�¼�������ǵ�����update���������׳��쳣
	 * 3.��update()��������һ���������ʱ�������session�Ļ������Ѿ�������ͬOID�ĳ־û����󣬻��׳��쳣
	 *   ��session�����У�����������OID��ͬ�Ķ���
	 */
	@Test
	public void testUpdate(){
		News news = (News) session.get(News.class, 1);
		
		transaction.commit();
		session.close();
		
//		news.setId(100);

		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
//		news.setAuthor("SUN"); //news��һ���������
		
		News news2 = (News) session.get(News.class, 1);
		session.update(news);
		//session������������OID��ͬ�Ķ����׳��쳣
	}
	
	/*
	 * 1.session.save()
	 * 1).ʹһ����ʱ�����Ϊ�־û�����
	 * 2).Ϊ�������ID
	 * 3).��flush����ʱ�ᷢ��һ��insert���
	 * 4).��save����֮ǰ��id����Ч��
	 * 5).�־û������ID�ǲ��ܱ��޸ĵ�
	 */
	/*
	 * persist():Ҳ��ִ��insert����
	 * ��save()������
	 * �ڵ���persist����֮ǰ���������Ѿ���ID�ˣ��Ͳ���ִ��insert,�����׳��쳣
	 */
	/*
	 * session.get()
	 * session.load()
	 * 1.ִ��get()���������ض��󣨽������ݿ��ѯ�������ݿ��ȡ���󣩣�����������
	 *   ִ��load�������������øö�����ִ�в�ѯ������������һ����������ӳټ�����
	 *   ���������������ݿ��ѯ��ֱ����Ҫ���˲ż��ض���
	 * 2.�������ݱ���û�еĶ�Ӧ�ļ�¼����sessionҲû�б��رգ�ͬʱ��Ҫʹ�ö���ʱ��
	 *   get: ����null
	 *   load: �������øö�����κ����ԣ�û���⣻����Ҫ��ʼ���ˣ��׳��쳣
	 * 3.load�������ܻ��׳�LazyInitializationException�쳣
	 *   ����Ҫ��ʼ���������֮ǰ�Ѿ��ر���session,�׳��������쳣
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
		news.setId(100); //��save()֮ǰsetID��Ч
		
		System.out.println(news);		
		session.save(news);
		System.out.println(news);
//		news.setId(101); //�־û������ID�ǲ��ܱ��޸ĵ�
	}
	
	@Test
	public void testClear() {
		News news1 = (News) session.get(News.class, 1);
		session.clear();
		News news2 = (News) session.get(News.class, 1);
	}
	
	/*
	 * connection.isolation
	 * ������뼶�� - 
	 */
	/*
	 * session.clear()
	 * ������
	 */
	/*
	 * session.refresh()
	 * refresh():��ǿ�Ʒ���select��䣬ʹsession�����ж����״̬�����ݱ��е�״̬һ����
	 */
	
	/*
	 * session.flush()
	 * flush:ʹ���ݱ��еļ�¼��Session�����еĶ����״̬����һ�£�Ϊ�˱���һ�£����ܻᷢ�Ͷ�Ӧ��SQL��䡣
	 * 1.��Transaction��commit()�����У��ȵ���session��flush���������ύ����
	 * 2.flush()�������ܻᷢ��SQL��䣬�������ύ����
	 * 3.ע�⣺��δ�ύ�������ʽ�ĵ���session.flush()����֮ǰ��Ҳ�п��ܻ����flush()������
	 * 1).ִ��HQL��QBC��ѯ�����Ƚ���flush()�������Եõ����ݱ�����µļ�¼��
	 * 2).����¼��ID���ɵײ����ݿ�ʹ�������ķ�ʽ���ɵģ����ڵ���save()�����󣬾ͻ���������insert���
	 * ��Ϊsave�����󣬱��뱣֤�����ID�Ǵ��ڵġ�
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
