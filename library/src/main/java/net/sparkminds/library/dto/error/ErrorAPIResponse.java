package net.sparkminds.library.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorAPIResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
	private String messageCode;
}
