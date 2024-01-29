package net.sparkminds.library.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sparkminds.library.dto.bookmana.BookManaResponse;
import net.sparkminds.library.entity.Author;
import net.sparkminds.library.entity.Book;
import net.sparkminds.library.entity.Category;
import net.sparkminds.library.enumration.EnumLanguage;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.exception.RequestException;
import net.sparkminds.library.repository.AuthorRepository;
import net.sparkminds.library.repository.BookRepository;
import net.sparkminds.library.repository.CategoryRepository;
import net.sparkminds.library.service.HandleCSVService;
import net.sparkminds.library.service.criteria.BookCriteria;
import net.sparkminds.library.service.query.BookQueryService;
import net.sparkminds.library.util.FileUtil;
import tech.jhipster.service.filter.StringFilter;

@Service
@RequiredArgsConstructor
@Log4j2
public class HandleCSVImpl implements HandleCSVService{
	
	private final MessageSource messageSource;
	private final BookQueryService bookQueryService;
	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final CategoryRepository categoryRepository;
	private static final String[] EXPECTED_HEADERS = {
            "title", "description", "totalpages", "availablecopies",
            "language", "publisher", "price",
            "authorid", "categoryid"
    };
	
	@Value("${BASEURL_RESOURCES}")
	private String BASEURL_RESOURCES;

	@Override
	@Transactional(rollbackFor = RequestException.class)
	public void importBookCSV(MultipartFile uploadfile) throws RequestException {
		String contentType = null;
		String message = null;
		List<Book> books = new ArrayList<>();
        Long line = (long) 0;
        String[] record;
        String[] actualHeaders;
        Optional<Author> author = null;
		Optional<Category> category = null;
		Long authorId = (long) 0;
		Long categoryId = (long) 0;
		String fileName = null;
		ClassPathResource classPathResource = new ClassPathResource("images/default.jpg");
        Path path = null;
        
		try {
			path = classPathResource.getFile().toPath();
		} catch (IOException e1) {
			message = messageSource.getMessage("file.image.default-failed", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"file.image.default-failed");
		}
		
		// Check size > 5MB
		if (uploadfile.getSize() > 5 * 1024 * 1024) { 
			message = messageSource.getMessage("file.file-sizeinvalid", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"file.file-sizeinvalid");
        }

        // Only accept CSV
		contentType = uploadfile.getContentType();
        if (contentType == null || !contentType.equals("text/csv")) {
        	message = messageSource.getMessage("file.file-invalid", 
					null, LocaleContextHolder.getLocale());
			
			log.error(message);
			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
					"file.file-invalid");
        }
 
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(uploadfile.getInputStream()))) {
        	actualHeaders = csvReader.readNext();
        	actualHeaders = Arrays.stream(actualHeaders)
        	        .map(String::trim)
        	        .map(String::toLowerCase)
        	        .toArray(String[]::new);
            if (!Arrays.equals(EXPECTED_HEADERS, actualHeaders)) {
            	message = messageSource.getMessage("file.csv.header-invalid", 
    					null, LocaleContextHolder.getLocale());
    			
    			log.error(message);
    			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
    					"file.csv.header-invalid");
            }
        	
        	while ((record = csvReader.readNext()) != null) {
        		line = csvReader.getLinesRead();
        		
        		int totalPagesValue = Integer.parseInt(record[2]);
        		int availableCopies = Integer.parseInt(record[3]);
        		BigDecimal priceValue = new BigDecimal(record[6]);
                if (totalPagesValue <= 1 || availableCopies <= 1 || priceValue.compareTo(BigDecimal.ZERO) <= 0) {
                	message = messageSource.getMessage("file.csv.number-invalid", 
        					null, LocaleContextHolder.getLocale());
        			
        			log.error(message + " " + line);
        			throw new RequestException(message + " " + line, HttpStatus.BAD_REQUEST.value(),
        					"file.csv.number-invalid");
                }
                
                if (StringUtils.isBlank(record[0]) || StringUtils.isBlank(record[1]) || 
                		StringUtils.isBlank(record[4]) || StringUtils.isBlank(record[5])) {
                	message = messageSource.getMessage("file.csv.string-invalid", 
        					null, LocaleContextHolder.getLocale());
        			
        			log.error(message + " " + line);
        			throw new RequestException(message + " " + line, HttpStatus.BAD_REQUEST.value(),
        					"file.csv.string-invalid");
                }
                
                authorId = Long.parseLong(record[7]);
                author = authorRepository.findById(authorId);
        		if(!author.isPresent()) {
        			message = messageSource.getMessage("author.id.id-notfound", 
        					null, LocaleContextHolder.getLocale());
        			
        			log.error(message + ": " + authorId);
        			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
        					"author.id.id-notfound");
        		}
        		
        		categoryId = Long.parseLong(record[8]);
        		category = categoryRepository.findById(categoryId);
        		if(!category.isPresent()) {
        			message = messageSource.getMessage("category.id.id-notfound", 
        					null, LocaleContextHolder.getLocale());
        			
        			log.error(message + ": " + categoryId);
        			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
        					"category.id.id-notfound");
        		}

        		File defaultImageFile = path.toFile();
				fileName = UUID.randomUUID().toString() + System.currentTimeMillis() + defaultImageFile.getName();
                
        		Book book = Book.builder()
						.title(record[0])
						.description(record[1])
						.totalPages(totalPagesValue)
						.availableCopies(availableCopies)
						.language(EnumLanguage.valueOf(record[4].toUpperCase()))
						.coverImageUrl("/images/books/" + fileName)
						.publisher(record[5])
						.price(priceValue)
						.status(EnumStatus.ACTIVE)
						.author(author.get())
						.category(category.get())
						.build();
   		 
        		books.add(book);
            }
        	 
        } catch (RequestException e) {
        	throw new RequestException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e.getMessageCode());
		} catch (IllegalArgumentException e) {
			message = messageSource.getMessage("file.csv.line-invalid", 
					null, LocaleContextHolder.getLocale());
			log.error(message + " " + line);
			throw new RequestException(message + " " + line, HttpStatus.BAD_REQUEST.value(), "file.csv.line-invalid");
	    } catch (Exception e) {
	    	log.error(e.getMessage());
			throw new RequestException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), "file.csv.csv-invalid");
		}  
        
        // Add book to DB
        createBooks(books, path);
	}
	
	private void createBooks(List<Book> books, Path path) {
		books.forEach(book -> {
    		StringFilter titleFilter = new StringFilter();
    		Page<BookManaResponse> bookresponses = null;
    		BookCriteria bookCriteria = new BookCriteria();
    		Pageable pageable = PageRequest.of(0, 10);
    		String message = null;
    		String fileName = null;
    		String fileDir = null;
    		
    		titleFilter.setEquals(book.getTitle());
    		bookCriteria.setTitle(titleFilter);
    		bookresponses = bookQueryService.findBookByCriteria(bookCriteria, pageable);
    		if(!bookresponses.isEmpty()) {
    			message = messageSource.getMessage("book.title.title-exists", 
    					null, LocaleContextHolder.getLocale());
    			
    			log.error(message + ": " + book.getTitle());
    			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
    					"book.title.title-exists");
    		}   
    		
    		try {
    			File defaultImageFile = path.toFile();
    			String prefix = "/images/books/";
    			fileName = book.getCoverImageUrl().substring(prefix.length());
        		fileDir = BASEURL_RESOURCES + "/images/books";
    			FileUtil.saveFile(fileDir, fileName, defaultImageFile);

            } catch (IOException e) {
            	message = messageSource.getMessage("file.image.createimage-failed", 
    					null, LocaleContextHolder.getLocale());
    			
    			log.error(message + ": " + fileDir + "/" + fileName);
    			throw new RequestException(message, HttpStatus.BAD_REQUEST.value(),
    					"file.image.createimage-failed");
            }
    		
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
        });
	}
}
