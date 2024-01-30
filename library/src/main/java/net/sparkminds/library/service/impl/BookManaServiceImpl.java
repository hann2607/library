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
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.bookmanagement.BookManaRequest;
import net.sparkminds.library.dto.bookmanagement.BookManaResponse;
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

	@Value("${baseUrl.Resources}")
	private String baseUrlResources;

	@Override
	public void createBookCriteria(BookManaRequest bookManaRequest) {
		Book book = null;
		StringFilter titleFilter = new StringFilter();
		Page<BookManaResponse> books = null;
		BookCriteria bookCriteria = new BookCriteria();
		Pageable pageable = PageRequest.of(0, 10);
		String message = null;

		titleFilter.setEquals(bookManaRequest.getTitle());
		bookCriteria.setTitle(titleFilter);
		books = bookQueryService.findBookByCriteria(bookCriteria, pageable);
		if (!books.isEmpty()) {
			message = messageSource.getMessage("book.title.title-exists", null, LocaleContextHolder.getLocale());

			log.error(message + ": " + bookManaRequest.getTitle());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "book.title.title-exists");
		}

		if (validateFile(bookManaRequest.getCoverImageFile())) {
			book = bookManaMapper.dtoToModel(bookManaRequest);
			book = handleUploadFile(bookManaRequest.getCoverImageFile(), book);

			bookRepository.save(book);
			message = messageSource.getMessage("book.insert-successed", null, LocaleContextHolder.getLocale());
			log.info(message + ": " + book.toString());
		}
	}

	private boolean validateFile(MultipartFile coverImageFile) {
		String contentType = null;
		String message = null;

		contentType = coverImageFile.getContentType();
		if (!("image/jpeg".equals(contentType)) && !("image/png".equals(contentType))) {
			message = messageSource.getMessage("bookmanarequest.coverImageFile.coverImageFile-invalid", null,
					LocaleContextHolder.getLocale());

			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"bookmanarequest.coverImageFile.coverImageFile-invalid");

		}

		return true;
	}

	private Book handleUploadFile(MultipartFile coverImageFile, Book book) {
		String fileName = null;
		String fileDir = null;
		String message = null;

		fileName = UUID.randomUUID().toString() + System.currentTimeMillis() + coverImageFile.getOriginalFilename();
		fileDir = baseUrlResources + "/images/books";
		try {
			FileUtil.saveMultipartFile(fileDir, fileName, coverImageFile);
		} catch (IOException e) {
			message = messageSource.getMessage("file.image.createimage-failed", null, LocaleContextHolder.getLocale());

			log.error(message + ": " + fileDir + "/" + fileName);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "file.image.createimage-failed");
		}

		book.setCoverImageUrl("/images/books/" + fileName);

		return book;
	}

	@Override
	public void updateBookCriteria(BookManaRequest bookManaRequest) {
		Book book = null;
		StringFilter titleFilter = new StringFilter();
		Page<BookManaResponse> books = null;
		Pageable pageable = PageRequest.of(0, 10);
		BookCriteria bookCriteria = new BookCriteria();
		String message = null;

		titleFilter.setEquals(bookManaRequest.getTitle());
		bookCriteria.setTitle(titleFilter);
		books = bookQueryService.findBookByCriteria(bookCriteria, pageable);
		if (books.isEmpty()) {
			message = messageSource.getMessage("book.book-notexists", null, LocaleContextHolder.getLocale());

			log.error(message + ": {} " + bookManaRequest.getTitle());
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "book.book-notexists");
		}

		if (validateFile(bookManaRequest.getCoverImageFile())) {
			book = bookManaMapper.dtoToModel(bookManaRequest);
			handleDeleteFile(book.getCoverImageUrl());
			book = handleUploadFile(bookManaRequest.getCoverImageFile(), book);
			book.setId(books.getContent().get(0).getId());

			bookRepository.save(book);
			message = messageSource.getMessage("book.update-successed", null, LocaleContextHolder.getLocale());
			log.info(message + ": {} " + book.toString());
		}
	}

	private void handleDeleteFile(String coverImageUrl) {
		String message = null;

		try {
			FileUtil.deleteFile(baseUrlResources + coverImageUrl);
		} catch (IOException e1) {
			message = messageSource.getMessage("file.image.deleteimage-failed", null, LocaleContextHolder.getLocale());

			log.error(message + ": " + coverImageUrl);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "file.image.deleteimage-failed");
		}
	}

	@Override
	public void deleteBookCriteria(Long id) {
		Optional<Book> book = null;
		String message = null;

		book = bookRepository.findById(id);
		if (!book.isPresent()) {
			message = messageSource.getMessage("book.book-notexists", null, LocaleContextHolder.getLocale());

			log.error(message + ": " + id);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "book.book-notexists");
		}

		if (book.get().getStatus().compareTo(EnumStatus.DELETED) == 0) {
			message = messageSource.getMessage("book.book-deleted", null, LocaleContextHolder.getLocale());

			log.error(message + ": " + id);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(), "book.book-deleted");
		}

		book.get().setStatus(EnumStatus.DELETED);
		bookRepository.save(book.get());
		message = messageSource.getMessage("book.delete-successed", null, LocaleContextHolder.getLocale());
		log.info(message + ": {} " + book.toString());
	}
}
