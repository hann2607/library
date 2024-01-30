package net.sparkminds.library.mapper;

import org.mapstruct.Mapper;

import net.sparkminds.library.dto.bookmanagement.BookManaRequest;
import net.sparkminds.library.dto.bookmanagement.BookManaResponse;
import net.sparkminds.library.entity.Book;

@Mapper
public interface BookManaMapper {
	
	BookManaResponse modelToDto(Book book);
	
	Book dtoToModel (BookManaRequest bookManaRequest);
	
}
