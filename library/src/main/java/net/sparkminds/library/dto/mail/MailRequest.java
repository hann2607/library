package net.sparkminds.library.dto.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {
	String from = "LibraryManage";
	String to;
	String[] cc;
	String[] bcc;
	String subject;
	String body;
	List<File> files = new ArrayList<>();

	public MailRequest(String to, String subject, String body) {
		this.to = to;
		this.subject = subject;
		this.body = body;

	}
}
