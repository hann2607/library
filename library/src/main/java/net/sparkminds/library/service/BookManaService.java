package net.sparkminds.library.service;

import net.sparkminds.library.dto.bookmana.BookManaRequest;

public interface BookManaService {
	void create(BookManaRequest bookManaRequest);
	
	void update(BookManaRequest bookManaRequest);
	
	void delete(Long id);
}
