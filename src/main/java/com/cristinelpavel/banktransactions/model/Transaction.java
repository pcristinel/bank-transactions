package com.cristinelpavel.banktransactions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "uuid")
	private UUID reference;

	@NotNull
	@Column(nullable = false)
	private String accountIban;

	private Instant date;

	@NotNull
	@Column(nullable = false)
	private BigDecimal amount;

	private BigDecimal fee;

	private String description;
}