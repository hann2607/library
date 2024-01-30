package net.sparkminds.library.dto.bookmanagement;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sparkminds.library.enumration.EnumLanguage;
import net.sparkminds.library.enumration.EnumStatus;
import net.sparkminds.library.service.validator.ValidateEnumValue;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookManaRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "{bookmanarequest.title.title-notblank}")
	@Length(max = 100, message = "{bookmanarequest.title.title-length}")
	private String title;

	@NotBlank(message = "{bookmanarequest.description.description-notblank}")
	@Length(max = 10000, message = "{bookmanarequest.description.description-length}")
	private String description;

	@Min(value = 1, message = "{bookmanarequest.totalPages.totalPages-min}")
	@NotNull(message = "{bookmanarequest.totalPages.totalPages-notnull}")
	private Integer totalPages;

	@Min(value = 0, message = "{bookmanarequest.availableCopies.availableCopies-min}")
	@NotNull(message = "{bookmanarequest.availableCopies.availableCopies-notnull}")
	private Integer availableCopies;

	@NotNull(message = "{bookmanarequest.language.language-invalid}")
	@ValidateEnumValue(enumClass = EnumLanguage.class, message = "{bookmanarequest.language.language-invalid}")
	private String language;

	@NotNull(message = "{bookmanarequest.coverImageFile.coverImageFile-invalid}")
	private MultipartFile coverImageFile;

	@NotBlank(message = "{bookmanarequest.publisher.publisher-notblank}")
	@Length(max = 100, message = "{bookmanarequest.publisher.publisher-length}")
	private String publisher;

	@DecimalMin(value = "1", message = "{bookmanarequest.price.price-min}")
	@NotNull(message = "{bookmanarequest.price.price-notnull}")
	private BigDecimal price;

	@NotNull(message = "{bookmanarequest.status.status-invalid}")
	@ValidateEnumValue(enumClass = EnumStatus.class, message = "{bookmanarequest.status.status-invalid}")
	private String status;

	@NotNull(message = "{bookmanarequest.authorId.authorId-notnull}")
	private Long authorId;

	@NotNull(message = "{bookmanarequest.categoryId.categoryId-notnull}")
	private Long categoryId;
}
