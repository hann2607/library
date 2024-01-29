package net.sparkminds.library.service;

import org.springframework.web.multipart.MultipartFile;

public interface HandleCSVService {
	void importBookCSV(MultipartFile uploadfile);
}
