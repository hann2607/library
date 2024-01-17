package net.sparkminds.library.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.mail.MailRequest;
import net.sparkminds.library.service.MailService;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	List<MailRequest> list = new ArrayList<>();
	private final JavaMailSender sender;

	@Override
	public void send(MailRequest mail) throws MessagingException {
		MimeMessage message = null;
		MimeMessageHelper helper = null;
		String[] cc = null;
		String[] bcc = null;

		message = sender.createMimeMessage();

		// Use Helper for setting info for message
		helper = new MimeMessageHelper(message, true, "utf-8");

		helper.setFrom(mail.getFrom());
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		helper.setText(mail.getBody() != null ? mail.getBody() : "", true);
		helper.setReplyTo(mail.getFrom());

		// Check arrString cc isPresent
		cc = mail.getCc();
		if (cc != null && cc.length > 0) {
			helper.setCc(cc);
		}

		// Check arrString bcc isPresent
		bcc = mail.getBcc();
		if (bcc != null && bcc.length > 0) {
			helper.setBcc(bcc);
		}

		// Check File
		List<File> files = mail.getFiles();
		if (files.size() > 0) {
			for (File file : files) {
				helper.addAttachment(file.getName(), file);
			}
		}

		// Send message to SMTP server
		sender.send(message);
	}

	@Override
	public void send(String to, String subject, String body) throws MessagingException {
		this.send(new MailRequest(to, subject, body));
	}

	@Override
	public void queue(MailRequest mail) {
		list.add(mail);
	}

	@Override
	public void queue(String to, String subject, String body) {
		queue(new MailRequest(to, subject, body));
	}

	@Scheduled(fixedDelay = 5000)
	public void run() {
		while (!list.isEmpty()) {
			MailRequest mail = list.remove(0);
			try {
				this.send(mail);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
