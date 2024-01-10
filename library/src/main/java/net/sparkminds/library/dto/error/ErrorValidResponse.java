package net.sparkminds.library.dto.error;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorValidResponse {
	private String timestamp;
	private int status;
	private String error;
	private List<FieldErrorInfo> errors;

	// The function generates ErrorResponse from HttpStatus and error information for the specific field
	public static ErrorValidResponse from(HttpStatus httpStatus, String fieldName, String message, String errorCode) {
		ErrorValidResponse errorResponse = new ErrorValidResponse();
		errorResponse.setTimestamp(LocalDateTime.now().toString());
		errorResponse.setStatus(httpStatus.value());
		errorResponse.setError(httpStatus.getReasonPhrase());
		errorResponse.addError(fieldName, message, errorCode);
		return errorResponse;
	}

	// The method creates a new error and adds it to the errors list
	public void addError(String fieldName, String message, String errorCode) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		errors.add(new FieldErrorInfo(fieldName, message, errorCode));
	}
}
