package net.sparkminds.library.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loanDetail")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanDetail extends Auditable{	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "price", nullable = false)
	private BigDecimal price;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "loan_id", nullable = false)
	private Loan loan;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Override
	public String toString() {
		return "LoanDetail [id=" + id + ", price=" + price + "]";
	}
}
