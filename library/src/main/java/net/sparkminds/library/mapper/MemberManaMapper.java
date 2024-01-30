package net.sparkminds.library.mapper;

import org.mapstruct.Mapper;

import net.sparkminds.library.dto.bookmanagement.BookManaRequest;
import net.sparkminds.library.dto.bookmanagement.BookManaResponse;
import net.sparkminds.library.dto.membermanagement.MemberManaRequest;
import net.sparkminds.library.dto.membermanagement.MemberManaResponse;
import net.sparkminds.library.entity.Book;
import net.sparkminds.library.entity.Customer;

@Mapper
public interface MemberManaMapper {
	
	MemberManaResponse modelToDto(Customer customer);
	
	Customer dtoToModel (MemberManaRequest memberManaRequest);
	
}
