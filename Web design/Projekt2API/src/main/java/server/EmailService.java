package server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailService {

	private static Logger logger = LogManager.getLogger(EmailService.class);

	private String subject;
	private String emailContent;

	private String userEmail;
	
	public boolean sendUserKey(String userEmail, String key) {
		this.userEmail = userEmail;
		subject = "din Q.C.A.P.I nyckel";
		emailContent = "Hej!\n\nHär kommer din nyckel till Q.C.A.P.I. "
				+ "\n\n" + key + "\n\nMed vänlig hälsning\nQ.C.A.P.I team";
		return buildMail();
	}
	
	public boolean sendAdminKey(String userEmail, String key) {
		this.userEmail = userEmail;
		subject = "Q.C.A.P.I admin nyckel";
		emailContent = "Hej!\n\nHär kommer din tillfälliga admin nyckel till Q.C.A.P.I."
				+ "\nLogga in och registrera användarnamn och lösenord.\n "
				+ "Nyckeln är giltig i 24 timmar"
				+ "\n\n" + key + "\n\nMed vänlig hälsning\nQ.C.A.P.I team";
		return buildMail();
	}

	private boolean buildMail() {
		logger.info("building email");
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.socketFactory.port", "587");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "587");
		
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(
						UserConfig.API_MAIL, UserConfig.API_MAIL_PASSWORD);
			}
			
		});
		
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(UserConfig.API_MAIL));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
			message.setSubject(subject);
			message.setText(emailContent);
		} catch (AddressException e) {
			logger.error("AdddressException", e);
			return false;
		} catch (MessagingException e) {
			logger.error("MessagingException", e);
			return false;
		}
		
		return sendMail(message);
	}
	
	private boolean sendMail(MimeMessage message) {
		try {
			Transport.send(message);
			logger.info("sending email");
			return true;
		} catch (MessagingException e) {
			logger.error("MessagingException sendMail", e);
			return false;
		}
	}

}
