package net.sparkminds.library.service;

import java.util.List;

import net.sparkminds.library.entity.Customer;

public interface MemberService {
	List<Customer> findCustomers();
}
