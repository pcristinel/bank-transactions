package com.cristinelpavel.banktransactions.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Builder
@Data
public class TransactionStatusRequestDTO {
	public enum Channel {
		CLIENT,
		ATM,
		INTERNAL
	}

	@NotNull
	private UUID reference;

	private Channel channel;
}