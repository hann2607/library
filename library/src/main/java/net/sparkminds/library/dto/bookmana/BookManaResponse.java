package net.sparkminds.library.dto.bookmana;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.enumration.EnumLanguage;
import net.sparkminds.library.enumration.EnumStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookManaResponse {
	private Long id;

	private String title;

	private String description;

	private Integer totalPages;

	private Integer availableCopies;

	private EnumLanguage language;

	private String coverImageUrl;

	private String publisher;

	private BigDecimal price;

	private EnumStatus status;

	private String authorFullname;

	private String categoryName;
	
	private LocalDateTime createdAt;
	
	private String createdBy;
	
	private LocalDateTime updatedAt;
	
	private String updatedBy;
}
