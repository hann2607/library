package net.sparkminds.library.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sparkminds.library.enumration.EnumLanguage;
import net.sparkminds.library.enumration.EnumStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class BookCriteria implements Serializable, Criteria{

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private StringFilter title;

	private StringFilter description;

	private IntegerFilter totalPages;
	
	private IntegerFilter availableCopies;
	
	private LanguageFilter language;
	
	private StringFilter publisher;
	
	private BigDecimalFilter price;
	
	private StatusFilter status;
	
	private LocalDateFilter createdAt;
	
	private LocalDateFilter updatedAt;
	
	private StringFilter createdBy;
	
	private StringFilter updatedBy;
	

	public BookCriteria(BookCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.title = other.title == null ? null : other.title.copy();
		this.description = other.description == null ? null : other.description.copy();
		this.totalPages = other.totalPages == null ? null : other.totalPages.copy();
		this.language = other.language == null ? null : other.language.copy();
		this.publisher = other.publisher == null ? null : other.publisher.copy();
		this.status = other.status == null ? null : other.status.copy();
		this.createdAt = other.createdAt == null ? null : other.createdAt.copy();
		this.updatedAt = other.updatedAt == null ? null : other.updatedAt.copy();
		this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
		this.updatedBy = other.updatedBy == null ? null : other.updatedBy.copy();
	}

	@Override
	public Criteria copy() {
		return new BookCriteria(this);
	}

	public static class LanguageFilter extends Filter<EnumLanguage> {

		private static final long serialVersionUID = 1L;

		public LanguageFilter() {
		}

		public LanguageFilter(LanguageFilter filter) {
			super(filter);
		}

		@Override
		public LanguageFilter copy() {
			return new LanguageFilter(this);
		}
	}
	
	public static class StatusFilter extends Filter<EnumStatus> {

		private static final long serialVersionUID = 1L;

		public StatusFilter() {
		}

		public StatusFilter(StatusFilter filter) {
			super(filter);
		}

		@Override
		public StatusFilter copy() {
			return new StatusFilter(this);
		}
	}

	@Override
	public String toString() {
		return "BookCriteria [id=" + id + ", title=" + title + ", description=" + description + ", totalPages="
				+ totalPages + ", availableCopies=" + availableCopies + ", language=" + language
				+ ", publisher=" + publisher + ", price=" + price + ", status=" + status
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookCriteria other = (BookCriteria) obj;
		return Objects.equals(availableCopies, other.availableCopies) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(createdBy, other.createdBy) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id) && Objects.equals(language, other.language)
				&& Objects.equals(price, other.price) && Objects.equals(publisher, other.publisher)
				&& Objects.equals(status, other.status) && Objects.equals(title, other.title)
				&& Objects.equals(totalPages, other.totalPages) && Objects.equals(updatedAt, other.updatedAt)
				&& Objects.equals(updatedBy, other.updatedBy);
	}

	@Override
	public int hashCode() {
		return Objects.hash(availableCopies, createdAt, createdBy, description, id, language, price,
				publisher, status, title, totalPages, updatedAt, updatedBy);
	}
}
