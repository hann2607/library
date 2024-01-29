package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.sparkminds.library.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan>{

}
