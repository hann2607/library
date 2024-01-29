package net.sparkminds.library.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import net.sparkminds.library.enumration.EnumStatus;

@Entity
@Table(name = "author")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Author extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "fullname", nullable = false, unique = false, length = 100)
	private String fullname;
	
	@Column(name = "dateOfBirth", nullable = false, unique = false)
	private LocalDate dateOfBirth;
	
	@Column(name = "biography", nullable = false, unique = false, length = 10000)
	private String biography;
	
	@Column(name = "avatar", nullable = false, unique = false, length = 255)
	private String avatar;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EnumStatus status;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "nationality_id", nullable = false)
	private Nationality nationality;

	@Override
	public String toString() {
		return "Author [id=" + id + ", fullname=" + fullname + ", dateOfBirth=" + dateOfBirth + ", biography="
				+ biography + ", avatar=" + avatar + ", status=" + status + "]";
	}
}
