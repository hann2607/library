package net.sparkminds.library.service;

import net.sparkminds.library.dto.bookmanagement.BookManaRequest;

public interface BookManaService {
	void createBookCriteria(BookManaRequest bookManaRequest);
	
	void updateBookCriteria(BookManaRequest bookManaRequest);
	
	void deleteBookCriteria(Long id);
}
