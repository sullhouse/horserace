package com.sullhouse.gambol;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

	public class EmailSender {
	
	public EmailSender() {
		super();
	}
	
	public void sendEmail(String to, String subject, String body) {

		final String username = "horserace@sullhouse.com";
		final String password = "BA#4dTPK";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.sullhouse.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			//message.setReplyTo(InternetAddress.parse("horserace@gmail.com"));
			message.setFrom(new InternetAddress("horserace@sullhouse.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(subject);
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}