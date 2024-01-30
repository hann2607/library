package net.sparkminds.library.service;

import java.util.List;

import net.sparkminds.library.dto.membermanagement.MemberManaRequest;
import net.sparkminds.library.entity.Customer;

public interface MemberManaService {
	List<Customer> findCustomers();
	
	void create(MemberManaRequest memberManaRequest);

	void update(MemberManaRequest memberManaRequest);

	void delete(Long id);
}
