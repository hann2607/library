package net.sparkminds.library.service;

import java.util.List;

import net.sparkminds.library.entity.Customer;

public interface CustomerService {
	List<Customer> findAll();
	
	Customer findById(Long id);
	
	void create(Customer user);
	
	void update(Customer user);

	Customer findByEmail(String email);
}
