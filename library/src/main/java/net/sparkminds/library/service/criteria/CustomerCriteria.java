package net.sparkminds.library.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sparkminds.library.enumration.EnumStatus;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CustomerCriteria implements Serializable, Criteria{

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private StringFilter firstName;

	private StringFilter lastName;

	private StringFilter phone;
	
	private StringFilter address;
	
	private StatusFilter status;
	

	public CustomerCriteria(CustomerCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.firstName = other.firstName == null ? null : other.firstName.copy();
		this.lastName = other.lastName == null ? null : other.lastName.copy();
		this.phone = other.phone == null ? null : other.phone.copy();
		this.address = other.address == null ? null : other.address.copy();
		this.status = other.status == null ? null : other.status.copy();
	}

	@Override
	public Criteria copy() {
		return new CustomerCriteria(this);
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerCriteria other = (CustomerCriteria) obj;
		return Objects.equals(address, other.address) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(phone, other.phone) && Objects.equals(status, other.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, firstName, id, lastName, phone, status);
	}

	@Override
	public String toString() {
		return "CustomerCriteria [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phone=" + phone
				+ ", address=" + address + ", status=" + status + "]";
	}
}
