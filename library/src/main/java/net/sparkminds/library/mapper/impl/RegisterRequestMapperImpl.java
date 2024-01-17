package net.sparkminds.library.mapper.impl;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Customer.CustomerBuilder;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.mapper.RegisterRequestMapper;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2024-01-08T22:16:01+0700", comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 1.4.300.v20221108-0856, environment: Java 18.0.2.1 (Oracle Corporation)")
@Service
@RequiredArgsConstructor
public class RegisterRequestMapperImpl implements RegisterRequestMapper {

	@Override
	public RegisterRequest modelToDto(Customer user) {
		if (user == null) {
			return null;
		}

		RegisterRequest userDTO = new RegisterRequest();

		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(user.getPassword());
		if (user.getStatus() != null) {
			userDTO.setStatus(user.getStatus().name());
		}

		return userDTO;
	}

	@Override
	public Customer dtoToModel(RegisterRequest userDTO) {
		if (userDTO == null) {
			return null;
		}

		CustomerBuilder<?, ?> user = Customer.builder();

		user.email(userDTO.getEmail());
		user.password(userDTO.getPassword());
		if (userDTO.getStatus() != null) {
			user.status(Enum.valueOf(EnumStatus.class, userDTO.getStatus()));
		}
		user.address(userDTO.getAddress());
		user.avatar(userDTO.getAvatar());
		user.firstname(userDTO.getFirstname());
		user.lastname(userDTO.getLastname());
		user.phone(userDTO.getPhone());

		return user.build();
	}
}
