package twisty.server.lib.service;

import twisty.server.lib.ServiceFactory;
import twisty.server.lib.service.impl.ServiceCryptImpl;
import junit.framework.TestCase;

public class ServiceCryptImplTest extends TestCase {
	
	public void testInit() throws Exception {
		ServiceCrypt i = (ServiceCrypt) new ServiceCryptImpl();
		i.init(ServiceFactory.SERVICE_CRYPT);
	}
	
	/** Tests SHA-512 hashes. */ 
	public void testSha512() throws Exception {
		ServiceCrypt i = (ServiceCrypt) new ServiceCryptImpl();
		i.init(ServiceFactory.SERVICE_CRYPT);
		i.setIterations(1);
		String src = "Hello World";
		String hashed = "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b";
		String hash = i.sha512(src);
		assertEquals(hashed, hash);
	    hash = i.sha512(src).toLowerCase(); // Not using a random salt?
		assertEquals(hashed, hash);
	    hash = i.sha512(src+".").toLowerCase(); // Not just pretending to work?
		assertFalse(hashed.equals(hash));
	}
	
	/** Tests password hashes. */ 
	public void testPasswd() throws Exception {
		ServiceCrypt i = (ServiceCrypt) new ServiceCryptImpl();
		i.init(ServiceFactory.SERVICE_CRYPT);
		String src = "Hello World";
		String passwd = i.passwd(src);
		assertTrue(i.check(src, passwd));
	}
}
