package net.sparkminds.library.service.impl;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.bookmana.BookManaRequest;
import net.sparkminds.library.dto.bookmana.BookManaResponse;
import net.sparkminds.library.entity.Book;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.BookManaMapper;
import net.sparkminds.library.repository.BookRepository;
import net.sparkminds.library.service.BookManaService;
import net.sparkminds.library.service.criteria.BookCriteria;
import net.sparkminds.library.service.query.BookQueryService;
import net.sparkminds.library.util.FileUtil;
import tech.jhipster.service.filter.StringFilter;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookManaServiceImpl implements BookManaService {
	
	private final MessageSource messageSource;
	private final BookManaMapper bookManaMapper;
	private final BookRepository bookRepository;
	private final BookQueryService bookQueryService;
	
	@Value("${BASEURL_RESOURCES}")
	private String BASEURL_RESOURCES;
	
	@Override
	@Transactional(rollbackFor = IOException.class)
	public void create(BookManaRequest bookManaRequest) {
		Book book = null;
		StringFilter titleFilter = new StringFilter();
		Page<BookManaResponse> books = null;
		BookCriteria bookCriteria = new BookCriteria();
		Pageable pageable = PageRequest.of(0, 10);
		String message = null;
		String contentType = null;
		String fileName = null;
		String fileDir = null;
		
		titleFilter.setEquals(bookManaRequest.getTitle());
		bookCriteria.setTitle(titleFilter);
		books = bookQueryService.findBookByCriteria(bookCriteria, pageable);
		if(!books.isEmpty()) {
			message = messageSource.getMessage("book.title.title-exists", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + bookManaRequest.getTitle());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.title.title-exists");
		}

		contentType = bookManaRequest.getCoverImageFile().getContentType();
		if (!("image/jpeg".equals(contentType)) && !("image/png".equals(contentType))) {
			message = messageSource.getMessage("bookmanarequest.coverImageFile.coverImageFile-invalid", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"bookmanarequest.coverImageFile.coverImageFile-invalid");
	    }      
		
		book = bookManaMapper.dtoToModel(bookManaRequest);
		fileName = UUID.randomUUID().toString() + System.currentTimeMillis() +
				bookManaRequest.getCoverImageFile().getOriginalFilename();
		fileDir = BASEURL_RESOURCES + "/images/books";
		try {
			FileUtil.saveMultipartFile(fileDir, fileName, bookManaRequest.getCoverImageFile());
		} catch (IOException e) {
			message = messageSource.getMessage("file.image.createimage-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + fileDir + "/" + fileName);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"file.image.createimage-failed");
		}
		
		book.setCoverImageUrl("/images/books/" + fileName);
		try {
			bookRepository.save(book);
			message = messageSource.getMessage("book.insert-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": " + book.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("book.insert-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + book.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.insert-failed");
		}
	}

	@Override
	public void update(BookManaRequest bookManaRequest) {
		Book book = null;
		StringFilter titleFilter = new StringFilter();
		Page<BookManaResponse> books = null;
		Pageable pageable = PageRequest.of(0, 10);
		BookCriteria bookCriteria = new BookCriteria();
		String message = null;
		String contentType = null;
		String fileName = null;
		String fileDir = null;
		
		titleFilter.setEquals(bookManaRequest.getTitle());
		bookCriteria.setTitle(titleFilter);
		books = bookQueryService.findBookByCriteria(bookCriteria, pageable);
		if(books.isEmpty()) {
			message = messageSource.getMessage("book.book-notexists", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": {} " + bookManaRequest.getTitle());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.book-notexists");
		}
		
		contentType = bookManaRequest.getCoverImageFile().getContentType();
		if (!("image/jpeg".equals(contentType)) && !("image/png".equals(contentType))) {
			message = messageSource.getMessage("bookmanarequest.coverImageFile.coverImageFile-invalid", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"bookmanarequest.coverImageFile.coverImageFile-invalid");
	    }  

		book = bookManaMapper.dtoToModel(bookManaRequest);
		fileName = UUID.randomUUID().toString() + System.currentTimeMillis() +
				bookManaRequest.getCoverImageFile().getOriginalFilename();
		fileDir = BASEURL_RESOURCES + "/images/books";
		try {
			FileUtil.saveMultipartFile(fileDir, fileName, bookManaRequest.getCoverImageFile());
		} catch (IOException e) {
			message = messageSource.getMessage("file.image.createimage-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + fileDir + "/" + fileName);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"file.image.createimage-failed");
		}
		
		try {
			FileUtil.deleteFile(BASEURL_RESOURCES + books.getContent().get(0).getCoverImageUrl());
		} catch (IOException e1) {
			message = messageSource.getMessage("file.image.deleteimage-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + fileDir + "/" + fileName);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"file.image.deleteimage-failed");
		}
		
		book.setId(books.getContent().get(0).getId());
		book.setCoverImageUrl("/images/books/" + fileName);
		try {
			bookRepository.save(book);
			message = messageSource.getMessage("book.update-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": {} " + book.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("book.update-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": {} " + book.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.update-failed");
		}
	}

	@Override
	public void delete(Long id) {
		Optional<Book> book = null;
		String message = null;

		book = bookRepository.findById(id);
		if(!book.isPresent()) {
			message = messageSource.getMessage("book.book-notexists", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + id);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.book-notexists");
		}
		
		if(book.get().getStatus().compareTo(EnumStatus.DELETED) == 0) {
			message = messageSource.getMessage("book.book-deleted", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + id);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.book-deleted");
		}

		book.get().setStatus(EnumStatus.DELETED);
		try {
			bookRepository.save(book.get());
			message = messageSource.getMessage("book.delete-successed", 
					null, LocaleContextHolder.getLocale());
			
			log.info(message + ": {} " + book.toString());
		} catch (Exception e) {
			message = messageSource.getMessage("book.delete-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": {} " + book.toString());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"book.delete-failed");
		}
	}
}
