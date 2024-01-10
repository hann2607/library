package net.sparkminds.library.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorAPIResponse {
	private String timestamp;
	private int status;
	private String error;
	private String message;
}
