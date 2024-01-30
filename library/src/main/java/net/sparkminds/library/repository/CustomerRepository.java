package net.sparkminds.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.enumration.EnumStatus;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer>{
	Optional<Customer> findByEmailAndStatus(String email, EnumStatus enumStatus);
}
