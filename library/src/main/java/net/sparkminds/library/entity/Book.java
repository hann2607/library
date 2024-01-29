package net.sparkminds.library.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sparkminds.library.enumration.EnumLanguage;
import net.sparkminds.library.enumration.EnumStatus;

@Entity
@Table(name = "book")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title", nullable = false, unique = true, length = 100)
	private String title;
	
	@Column(name = "description", nullable = false, unique = false, length = 10000)
	private String description;
	
	@Column(name = "totalPages")
	private Integer totalPages;
	
	@Column(name = "availableCopies")
	private Integer availableCopies;
	
	@Column(name = "language", nullable = false)
	@Enumerated(EnumType.STRING)
	private EnumLanguage language;
	
	@Column(name = "coverImageUrl", nullable = false, unique = false)
	private String coverImageUrl;
	
	@Column(name = "publisher", nullable = false, unique = false, length = 100)
	private String publisher;
	
	@Column(name = "price")
	private BigDecimal price;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EnumStatus status;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	@JsonIgnore
    @OneToMany(mappedBy="book", fetch = FetchType.LAZY)
    private List<LoanDetail> loanDetails;

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", description=" + description + ", totalPages=" + totalPages
				+ ", availableCopies=" + availableCopies + ", language=" + language + ", coverImageUrl=" + coverImageUrl
				+ ", publisher=" + publisher + ", price=" + price + ", status=" + status + "]";
	}
}
