package net.sparkminds.library.restcontroller.privates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.library.dto.bookmanagement.BookManaRequest;
import net.sparkminds.library.dto.bookmanagement.BookManaResponse;
import net.sparkminds.library.service.BookManaService;
import net.sparkminds.library.service.BookCsvImportService;
import net.sparkminds.library.service.criteria.BookCriteria;
import net.sparkminds.library.service.query.BookQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "Book management", description = "Book management APIs")
public class BookManaRestController {
	private final BookQueryService bookQueryService;
	private final BookManaService bookManaService;
	private final BookCsvImportService handleCSVService;
	
	@Operation(summary = "Search book", 
			description = "The response is list book.", 
			tags = { "Book management", "get" })
	@GetMapping("/books")
	public ResponseEntity<Page<BookManaResponse>> searchBook(BookCriteria bookCriteria, Pageable pageable) {
		return ResponseEntity.ok(bookQueryService.findBookByCriteria(bookCriteria, pageable));
	}
	
	@Operation(summary = "Create new book", 
			description = "The response is BookManaRequest.", 
			tags = { "Book management", "post" })
	@PostMapping(value = "/books", consumes = { "multipart/form-data" })
	public ResponseEntity<Void> createBook(@Valid @ModelAttribute BookManaRequest bookManaRequest) {
		bookManaService.createBookCriteria(bookManaRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Update book", 
			description = "The response is BookManaRequest.", 
			tags = { "Book management", "put" })
	@PutMapping(value = "/books", consumes = { "multipart/form-data" })
	public ResponseEntity<Void> updateBook(@Valid @ModelAttribute BookManaRequest bookManaRequest) {
		bookManaService.updateBookCriteria(bookManaRequest);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Delete book", 
			description = "Delete book by id.", 
			tags = { "Book management", "delete" })
	@DeleteMapping("/books/{id}")
	public ResponseEntity<Void> updateBook(@PathVariable(name = "id") Long id) {
		bookManaService.deleteBookCriteria(id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Import book", 
	description = "Import book from file CSV.", 
	tags = { "Book management", "post" })
	@PostMapping(value = "/books/csv", consumes = { "multipart/form-data" })
    public ResponseEntity<Void> importBooks(@RequestParam("file") MultipartFile uploadfile) {
		handleCSVService.importBookCSV(uploadfile);
		return ResponseEntity.ok().build(); 
    }
}
