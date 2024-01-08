package net.sparkminds.library.mapper.impl;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Service;

import net.sparkminds.library.dto.UserDTO;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.mapper.UserMapper;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-08T17:22:04+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 1.4.300.v20221108-0856, environment: Java 18.0.2.1 (Oracle Corporation)"
)
@Service
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO modelToDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setBlockedAt( user.getBlockedAt() );
        userDTO.setEmail( user.getEmail() );
        userDTO.setId( user.getId() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setReasonBlocked( user.getReasonBlocked() );
        userDTO.setStatus( user.getStatus() );

        return userDTO;
    }

    @Override
    public User dtoToModel(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User.UserBuilder<?, ?> user = User.builder();

        user.blockedAt( userDTO.getBlockedAt() );
        user.email( userDTO.getEmail() );
        user.id( userDTO.getId() );
        user.password( userDTO.getPassword() );
        user.reasonBlocked( userDTO.getReasonBlocked() );
        user.status( userDTO.getStatus() );
        user.address( userDTO.getAddress() );
        user.firstname( userDTO.getFirstname() );
        user.lastname( userDTO.getLastname() );
        user.middlename( userDTO.getMiddlename() );
        user.phone( userDTO.getPhone() );

        return user.build();
    }
}
