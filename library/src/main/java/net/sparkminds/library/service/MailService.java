package net.sparkminds.library.service;

import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import net.sparkminds.library.dto.mail.MailRequest;


@Service
public interface MailService {
	void send(MailRequest mail) throws MessagingException;

	void send(String to, String subject, String body) throws MessagingException;
	
	void queue(MailRequest mail);
	
	void queue(String to, String subject, String body);

}
