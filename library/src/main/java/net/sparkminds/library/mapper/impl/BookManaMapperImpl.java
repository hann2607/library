package net.sparkminds.library.mapper.impl;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.bookmanagement.BookManaRequest;
import net.sparkminds.library.dto.bookmanagement.BookManaResponse;
import net.sparkminds.library.entity.Author;
import net.sparkminds.library.entity.Book;
import net.sparkminds.library.entity.Category;
import net.sparkminds.library.enumration.EnumLanguage;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.mapper.BookManaMapper;
import net.sparkminds.library.repository.AuthorRepository;
import net.sparkminds.library.repository.CategoryRepository;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2024-01-24T17:30:56+0700", comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 1.4.300.v20221108-0856, environment: Java 18.0.2.1 (Oracle Corporation)")
@Service
@RequiredArgsConstructor
@Log4j2
public class BookManaMapperImpl implements BookManaMapper {

	private final AuthorRepository authorRepository;
	private final CategoryRepository categoryRepository;
	private final MessageSource messageSource;
	
	@Override
	public BookManaResponse modelToDto(Book book) {
		if (book == null) {
			return null;
		}

		BookManaResponse bookManaResponse = new BookManaResponse();

		bookManaResponse.setAvailableCopies(book.getAvailableCopies());
		bookManaResponse.setCoverImageUrl(book.getCoverImageUrl());
		bookManaResponse.setCreatedAt(book.getCreatedAt());
		bookManaResponse.setCreatedBy(book.getCreatedBy());
		bookManaResponse.setDescription(book.getDescription());
		bookManaResponse.setId(book.getId());
		bookManaResponse.setLanguage(book.getLanguage());
		bookManaResponse.setPrice(book.getPrice());
		bookManaResponse.setPublisher(book.getPublisher());
		bookManaResponse.setStatus(book.getStatus());
		bookManaResponse.setTitle(book.getTitle());
		bookManaResponse.setTotalPages(book.getTotalPages());
		bookManaResponse.setUpdatedAt(book.getUpdatedAt());
		bookManaResponse.setUpdatedBy(book.getUpdatedBy());
		bookManaResponse.setAuthorFullname(book.getAuthor().getFullname());
		bookManaResponse.setCategoryName(book.getCategory().getCategoryName());
		bookManaResponse.setCreatedBy(book.getCreatedBy());
		bookManaResponse.setUpdatedBy(book.getUpdatedBy());

		return bookManaResponse;
	}

	@Override
	public Book dtoToModel(BookManaRequest bookManaRequest) {
		Optional<Author> author = null;
		Optional<Category> category = null;
		String message = null;
		
		if (bookManaRequest == null) {
			return null;
		}

		Book book = new Book();

		BeanUtils.copyProperties(bookManaRequest, book);
		
		author = authorRepository.findById(bookManaRequest.getAuthorId());
		if(!author.isPresent()) {
			message = messageSource.getMessage("author.id.id-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + bookManaRequest.getAuthorId());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"author.id.id-notfound");
		}
		
		category = categoryRepository.findById(bookManaRequest.getCategoryId());
		if(!category.isPresent()) {
			message = messageSource.getMessage("category.id.id-notfound", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message + ": " + bookManaRequest.getCategoryId());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"category.id.id-notfound");
		}
		
		book.setLanguage(Enum.valueOf(EnumLanguage.class, bookManaRequest.getLanguage().toUpperCase()));
		book.setStatus(Enum.valueOf(EnumStatus.class, bookManaRequest.getStatus().toUpperCase()));
		book.setAuthor(author.get());
		book.setCategory(category.get());
		
		return book;
	}
}
