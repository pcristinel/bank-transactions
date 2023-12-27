package com.cristinelpavel.banktransactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
