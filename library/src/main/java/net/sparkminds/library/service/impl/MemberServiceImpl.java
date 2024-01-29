package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.service.MemberService;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final CustomerRepository customerRepository;
	
	@Override
	public List<Customer> findCustomers() {
		return customerRepository.findAll();
	}

}
