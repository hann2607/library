package net.sparkminds.library.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.membermanagement.MemberManaRequest;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.service.MemberManaService;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberManaService {
	
	private final CustomerRepository customerRepository;
	
	@Override
	public List<Customer> findCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public void create(MemberManaRequest memberManaRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(MemberManaRequest memberManaRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

}
