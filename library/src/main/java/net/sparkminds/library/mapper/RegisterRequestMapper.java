package net.sparkminds.library.mapper;

import org.mapstruct.Mapper;

import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.User;

@Mapper
public interface RegisterRequestMapper {
	
	RegisterRequest modelToDto(User user);
	
	User dtoToModel (RegisterRequest userDTO);
	
}
