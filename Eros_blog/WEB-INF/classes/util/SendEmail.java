package util;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import data.EmailContent;
import data.Manager;

/**
 * 透過管理員電子郵件發 email
 * 
 * @author Alan
 * 
 */
public class SendEmail {

	/**
	 * @param args
	 */
	public static void main(String args[]) {

		Manager manager = new Manager();
		manager.setEmail("");
		manager.setEmail_pw("");
		manager.setSmtpServer("smtp.gmail.com");
		manager.setSmtpServer_port("587");
		EmailContent email = new EmailContent();
		email.setReceiver("");
		email.setTitle("Test");
		email.setBody("This is a test mail");
		try {
			SendManagerMail(manager, email, false);
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param manager
	 * @param emailContent
	 * @throws EmailException
	 * @throws NoSuchProviderException
	 * @throws MessagingException
	 */
	public static void SendManagerMail(Manager manager, EmailContent emailContent, boolean notifyManager) throws EmailException, NoSuchProviderException, MessagingException {

		Logger logger = Logger.getLogger("SendEmail");// logger
		Calendar start = Calendar.getInstance();

		String managerAccount = manager.getEmail().substring(0, manager.getEmail().indexOf("@"));

		// 設定 Commons Email
		HtmlEmail email = new HtmlEmail();
		// email.setDebug(true);
		email.setCharset("UTF-8");
		email.setHostName(manager.getSmtpServer());
		email.setSmtpPort(Integer.parseInt(manager.getSmtpServer_port()));
		email.setAuthenticator(new DefaultAuthenticator(managerAccount, manager.getEmail_pw()));
		email.setTLS(true);
		email.setFrom(manager.getEmail());
		email.setSubject(emailContent.getTitle());
		email.setHtmlMsg(emailContent.getBody());
		email.addTo(emailContent.getReceiver());
		if (notifyManager) {
			email.addTo(manager.getEmail());
		}
		email.buildMimeMessage();
		Message m = email.getMimeMessage();

		// 但使用JavaMail來寄，速度較快
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		Session mailSession = Session.getDefaultInstance(props);
		Transport transport = mailSession.getTransport("smtp");
		transport.connect(manager.getSmtpServer(), Integer.parseInt(manager.getSmtpServer_port()), managerAccount, manager.getEmail_pw());
		transport.sendMessage(m, m.getAllRecipients());

		Calendar end = Calendar.getInstance();
		logger.info("Email 寄送時間: " + String.valueOf(end.getTimeInMillis() - start.getTimeInMillis()) + " 毫秒");
	}

}
