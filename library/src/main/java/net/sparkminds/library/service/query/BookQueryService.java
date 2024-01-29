package net.sparkminds.library.service.query;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.bookmana.BookManaResponse;
import net.sparkminds.library.entity.Book;
import net.sparkminds.library.entity.Book_;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.BookManaMapper;
import net.sparkminds.library.repository.BookRepository;
import net.sparkminds.library.service.criteria.BookCriteria;
import tech.jhipster.service.QueryService;

@Service
@Transactional(readOnly = true)
@Log4j2
@RequiredArgsConstructor
public class BookQueryService extends QueryService<Book>{

	private final BookRepository bookRepository;
	private final BookManaMapper bookManaMapper;
	private final MessageSource messageSource;
	
	@Transactional(readOnly = true)
    public Page<BookManaResponse> findBookByCriteria(BookCriteria criteria, Pageable pageable) {
        log.debug("find book by criteria : {}", criteria);
        final Specification<Book> specification = createSpecification(criteria);
        Page<Book> books = bookRepository.findAll(specification, pageable);
        List<BookManaResponse> bookManaResponses = new ArrayList<>();
        books.getContent().forEach(book -> bookManaResponses.add(bookManaMapper.modelToDto(book)));
        Page<BookManaResponse> bookManaResponsePage = new PageImpl<>(bookManaResponses, pageable, books.getTotalElements());
        return bookManaResponsePage;
    }
	
	protected Specification<Book> createSpecification(BookCriteria criteria) {
		String message = null;
        Specification<Book> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Book_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Book_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Book_.description));
            }
            if (criteria.getTotalPages() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalPages(), Book_.totalPages));
            }
            if (criteria.getAvailableCopies() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAvailableCopies(), Book_.availableCopies));
            }
            if (criteria.getLanguage() != null) {
                specification = specification.and(buildSpecification(criteria.getLanguage(), Book_.language));
            }
            if (criteria.getPublisher() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPublisher(), Book_.publisher));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), Book_.price));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Book_.status));
            }
            if(criteria.getCreatedAt() != null) {
            	
            	if(criteria.getCreatedAt().getGreaterThanOrEqual().isAfter(criteria.getCreatedAt().getLessThanOrEqual())) {
            		message = messageSource.getMessage("book.createdat-createat-invalid", 
    						null, LocaleContextHolder.getLocale());
    				
    				log.error(message + ", Start time: " + criteria.getCreatedAt().getGreaterThanOrEqual() 
    						+ ", End Time: " + criteria.getCreatedAt().getLessThanOrEqual());
    				throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
    						"book.createdat-createat-invalid");
            	}
            	specification = specification.and((root, query, builder) 
            			-> builder.between(root.get(Book_.createdAt), 
            					criteria.getCreatedAt().getGreaterThanOrEqual().atStartOfDay(), 
            					criteria.getCreatedAt().getLessThanOrEqual().atTime(LocalTime.MAX)));
            }
            if(criteria.getUpdatedAt() != null) {
            	specification = specification.and((root, query, builder) 
            			-> builder.between(root.get(Book_.updatedAt), 
            					criteria.getCreatedAt().getGreaterThanOrEqual().atStartOfDay(), 
            					criteria.getCreatedAt().getLessThanOrEqual().atTime(LocalTime.MAX)));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Book_.createdBy));
            }
            if (criteria.getUpdatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUpdatedBy(), Book_.updatedBy));
            }
        }
        return specification;
    }
}
