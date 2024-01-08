package net.sparkminds.library.mapper;

import org.mapstruct.Mapper;

import net.sparkminds.library.dto.UserDTO;
import net.sparkminds.library.entity.User;

@Mapper
public interface UserMapper {
	
	UserDTO modelToDto(User user);
	
	User dtoToModel (UserDTO userDTO);
	
}
