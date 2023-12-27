package com.cristinelpavel.banktransactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

	@Id
	private String iban;

	@NotNull
	@Column(nullable = false)
	private BigDecimal accountBalance;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Account account = (Account) o;
		return iban != null && Objects.equals(iban, account.iban);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
