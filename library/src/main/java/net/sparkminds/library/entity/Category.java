package net.sparkminds.library.entity;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sparkminds.library.enumration.EnumStatus;

@Entity
@Table(name = "category")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category extends Auditable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "categoryName", nullable = false, unique = true, length = 100)
	private String categoryName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EnumStatus status;
	
	@JsonIgnore
    @OneToMany(mappedBy="category", fetch = FetchType.LAZY)
    private List<Book> books;

	@Override
	public String toString() {
		return "Category [id=" + id + ", categoryName=" + categoryName + ", status=" + status + "]";
	}
}
