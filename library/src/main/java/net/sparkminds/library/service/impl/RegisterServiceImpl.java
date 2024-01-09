package net.sparkminds.library.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.register.RegisterRequest;
import net.sparkminds.library.entity.Account;
import net.sparkminds.library.entity.User;
import net.sparkminds.library.enums.EnumStatus;
import net.sparkminds.library.mapper.RegisterRequestMapper;
import net.sparkminds.library.repository.UserRepository;
import net.sparkminds.library.service.AccountService;
import net.sparkminds.library.service.EncryptionService;
import net.sparkminds.library.service.RegisterService;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class RegisterServiceImpl implements RegisterService {

	private final UserRepository userRepository;
	private final RegisterRequestMapper userMapper;
	private final EncryptionService encryptionService;
	private final AccountService accountService;
	private final MessageSource messageSource;

	@Override
	public void register(RegisterRequest userDTO) {
		List<Account> accounts = new ArrayList<>();
		User user = null;
		user = userMapper.dtoToModel(userDTO);
		accounts = accountService.findByEmail(user.getEmail());
		if (accounts.isEmpty()) {
			user.setPassword(encryptionService.encrypt(user.getPassword()));
			user.setVerify(false);
			user.setStatus(EnumStatus.ACTIVE);
			userRepository.save(user);
			log.info("Insert Account successfully: " + user);
		} else {
			log.error("Register Account failed, because Account is exists: " + user);
			String errorMessage = messageSource.getMessage("Duplicate.Account", null, LocaleContextHolder.getLocale());
			throw new RuntimeException(errorMessage);
		}
	}
}
