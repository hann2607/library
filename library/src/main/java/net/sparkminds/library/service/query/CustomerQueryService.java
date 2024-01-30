package net.sparkminds.library.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.membermanagement.MemberManaResponse;
import net.sparkminds.library.entity.Customer;
import net.sparkminds.library.entity.Customer_;
import net.sparkminds.library.mapper.MemberManaMapper;
import net.sparkminds.library.repository.CustomerRepository;
import net.sparkminds.library.service.criteria.CustomerCriteria;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
@Log4j2
@RequiredArgsConstructor
public class CustomerQueryService extends QueryService<Customer>{

	private final CustomerRepository customerRepository;
	private final MemberManaMapper memberManaMapper;
	
	@Transactional(readOnly = true)
    public Page<MemberManaResponse> findMemberByCriteria(CustomerCriteria criteria, Pageable pageable) {
        log.debug("find customer by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        Page<Customer> members = customerRepository.findAll(specification, pageable);
        Page<MemberManaResponse> memberManaResponsePage = members.map(member -> memberManaMapper.modelToDto(member));
        return memberManaResponsePage;
    }
	
	protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Customer_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Customer_.firstname));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Customer_.lastname));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Customer_.phone));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Customer_.address));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Customer_.status));
            }
        }
        return specification;
    }
}
