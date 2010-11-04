package twisty.server.lib.service;

import java.util.HashMap;

import twisty.server.lib.ServiceFactory;
import twisty.server.lib.service.impl.ServiceSmtpImpl;
import junit.framework.TestCase;

public class ServiceSmtpImplTest extends TestCase {
	
	public void testInit() throws Exception {
		ServiceSmtp i = (ServiceSmtp) new ServiceSmtpImpl();
		i.init(ServiceFactory.SERVICE_SMTP);
	}
	
	public void testTemplate() throws Exception {
		ServiceSmtp i = (ServiceSmtp) new ServiceSmtpImpl();
		i.init(ServiceFactory.SERVICE_SMTP);
		HashMap<String, String> template = i.template();
		assertNotNull(template);
	}
	
	public void testSend() throws Exception {
		ServiceSmtp i = (ServiceSmtp) new ServiceSmtpImpl();
		i.init(ServiceFactory.SERVICE_SMTP);
		HashMap<String, String> msg = i.template();
		msg.put(ServiceSmtp.TO, "douglasl@staff.iinet.net.au");
		msg.put(ServiceSmtp.FROM, "linderd@iinet.net.au");
		msg.put(ServiceSmtp.HOST, "mail.iinet.net.au");
		msg.put(ServiceSmtp.AUTH_USERNAME, "linderd");
		msg.put(ServiceSmtp.AUTH_PASSWORD, "XXX");
		msg.put(ServiceSmtp.SUBJECT, "Hello");
		msg.put(ServiceSmtp.BODY, "<b>World</b>");
		msg.put(ServiceSmtp.MIME_TYPE, "text/html");
		i.send(msg);
	}
}
