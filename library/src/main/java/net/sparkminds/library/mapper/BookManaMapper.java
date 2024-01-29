package net.sparkminds.library.mapper;

import org.mapstruct.Mapper;

import net.sparkminds.library.dto.bookmana.BookManaRequest;
import net.sparkminds.library.dto.bookmana.BookManaResponse;
import net.sparkminds.library.entity.Book;

@Mapper
public interface BookManaMapper {
	
	BookManaResponse modelToDto(Book book);
	
	Book dtoToModel (BookManaRequest bookManaRequest);
	
}
