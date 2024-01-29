package net.sparkminds.library.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import net.sparkminds.library.enumration.EnumLoan;

@Entity
@Table(name = "loan")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loan extends Auditable{	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "loanDate", nullable = false)
	private LocalDateTime loanDate;
	
	@Column(name = "returnDate", nullable = false)
	private LocalDateTime returnDate;
	
	@Column(name = "actualReturnDate", nullable = true)
	private LocalDateTime actualReturnDate;
	
	@Column(name = "totalPrice")
	private BigDecimal totalPrice;
	
	@Column(name = "note", nullable = true, unique = false, length = 255)
	private String note;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, unique = false)
	private EnumLoan status;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;
	
	@JsonIgnore
    @OneToMany(mappedBy="loan", fetch = FetchType.LAZY)
    private List<LoanDetail> loanDetails;

	@Override
	public String toString() {
		return "Loan [id=" + id + ", loanDate=" + loanDate + ", returnDate=" + returnDate + ", totalPrice=" + totalPrice
				+ ", note=" + note + ", status=" + status + "]";
	}
}
