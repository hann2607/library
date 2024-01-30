package net.sparkminds.library.service;

import org.springframework.web.multipart.MultipartFile;

public interface BookCsvImportService {
	void importBookCSV(MultipartFile uploadfile);
}
