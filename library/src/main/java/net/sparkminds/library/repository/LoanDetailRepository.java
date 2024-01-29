package net.sparkminds.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.sparkminds.library.entity.LoanDetail;

public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long>, JpaSpecificationExecutor<LoanDetail>{

}
