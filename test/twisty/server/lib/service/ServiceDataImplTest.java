package twisty.server.lib.service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.*;
import twisty.server.lib.ServiceFactory;
import twisty.server.lib.service.impl.ServiceDataImpl;
import junit.framework.TestCase;

public class ServiceDataImplTest extends TestCase {
	
	public void testInit() throws Exception {
		ServiceData i = (ServiceData) new ServiceDataImpl();
		i.init(ServiceFactory.SERVICE_DATA);
	}
	
	/** Tests putting an object. */
	public void testPersist() throws Exception {
		ServiceData i = (ServiceData) new ServiceDataImpl();
		i.init(ServiceFactory.SERVICE_DATA);
		i.registerClass(ServiceDataImplTestSample.class);
		HashMap<String, String> properties = i.getConnectionTemplate();
		properties.put(ServiceData.USERNAME, "root");
		properties.put(ServiceData.PASSWORD, "admin");
		properties.put(ServiceData.URL, "localhost/example");
		i.connect(properties);
		EntityManager em = i.getEntityManager();
		
		ServiceDataImplTestSample p = new ServiceDataImplTestSample();
		p.name = "Doug";
		em.getTransaction().begin();
		em.persist(p);
		em.getTransaction().commit();
	}
		
	/** Tests fetching an object. */
	public void testFetch() throws Exception {
		ServiceData i = (ServiceData) new ServiceDataImpl();
		i.init(ServiceFactory.SERVICE_DATA);
		i.registerClass(ServiceDataImplTestSample.class);
		HashMap<String, String> properties = i.getConnectionTemplate();
		properties.put(ServiceData.USERNAME, "root");
		properties.put(ServiceData.PASSWORD, "admin");
		properties.put(ServiceData.URL, "localhost/example");
		i.connect(properties);
		EntityManager em = i.getEntityManager();
		
		Query q = em.createQuery("Select p from ServiceDataImplTestSample p where p.name = :name");
		q.setParameter("name", "Doug");
		List<?> item = q.getResultList();
		assertNotNull(item.size() > 0);
	}
	
	/** Tests deleting an object. */
	public void testDelete() throws Exception {
		ServiceData i = (ServiceData) new ServiceDataImpl();
		i.init(ServiceFactory.SERVICE_DATA);
		i.registerClass(ServiceDataImplTestSample.class);
		HashMap<String, String> properties = i.getConnectionTemplate();
		properties.put(ServiceData.USERNAME, "root");
		properties.put(ServiceData.PASSWORD, "admin");
		properties.put(ServiceData.URL, "localhost/example");
		i.connect(properties);
		EntityManager em = i.getEntityManager();
		
		Query q = em.createQuery("Select p from ServiceDataImplTestSample p where p.name = :name");
		q.setParameter("name", "Doug");
		List<?> item = q.getResultList();
		ServiceDataImplTestSample target = (ServiceDataImplTestSample) item.get(0);
		assertNotNull(item.size() > 0);
		em.getTransaction().begin();
		em.remove(target);
		em.getTransaction().commit();
	}
}
