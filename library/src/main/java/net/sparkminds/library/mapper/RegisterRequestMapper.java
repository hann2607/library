package net.sparkminds.library.mapper;

import org.mapstruct.Mapper;

import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.Customer;

@Mapper
public interface RegisterRequestMapper {
	
	RegisterRequest modelToDto(Customer user);
	
	Customer dtoToModel (RegisterRequest userDTO);
	
}
