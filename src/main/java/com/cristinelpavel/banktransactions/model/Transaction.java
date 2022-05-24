package com.cristinelpavel.banktransactions.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Transaction {

	@Id
	@GeneratedValue
	private Long reference;

	private String accountIban;

	private Instant date;

	private Double amount;

	private Double fee;

	private String description;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Transaction that = (Transaction) o;
		return reference != null && Objects.equals(reference, that.reference);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}