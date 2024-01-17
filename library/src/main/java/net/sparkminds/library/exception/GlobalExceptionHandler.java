package net.sparkminds.library.exception;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import net.sparkminds.library.dto.error.ErrorAPIResponse;
import net.sparkminds.library.dto.error.ErrorValidResponse;
import net.sparkminds.library.dto.error.FieldErrorInfo;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Common method to handle both RequestException
	public static ErrorAPIResponse createErrorResponse(int statusCode, String message, String messageCode) {		
		ErrorAPIResponse errorAPIResponse = ErrorAPIResponse.builder()
				.timestamp(Instant.now().toString())
				.status(HttpStatus.valueOf(statusCode).value())
				.error(HttpStatus.valueOf(statusCode).getReasonPhrase())
				.message(message)
				.messageCode(messageCode).build();

		return errorAPIResponse;
	}

    @ExceptionHandler({RequestException.class, ExpiredJwtException.class})
    public ResponseEntity<ErrorAPIResponse> handleCustomException(RequestException e) {
        ErrorAPIResponse errorAPIResponse = createErrorResponse(e.getStatusCode(), e.getMessage(), e.getMessageCode());
        return new ResponseEntity<>(errorAPIResponse, HttpStatus.valueOf(e.getStatusCode()));
    }
    
	// Error for validation
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public static ResponseEntity<ErrorValidResponse> handleValidationException(MethodArgumentNotValidException ex) {
		List<FieldError> errors = ex.getBindingResult().getFieldErrors();
		List<FieldErrorInfo> errorInfos = new ArrayList<>();
		String errorCode = null;
		
		for (FieldError error : errors) {
			String field = error.getField();
			String message = error.getDefaultMessage();
			FieldErrorInfo errorInfo = new FieldErrorInfo();
			
			if(error.getCode().equals("ValidPhoneNumber") || error.getCode().equals("Email") 
					|| error.getCode().equals("Pattern")) {
				errorCode = "Invalid";
			} else {
				errorCode = error.getCode();
			}
			
			errorInfo.setErrorCode(error.getObjectName() + "." + error.getField() + "." 
					+ error.getField() + "-" + errorCode);
			errorInfo.setField(field);
			errorInfo.setMessage(message);
			errorInfos.add(errorInfo);
		}

		ErrorValidResponse errorResponse = new ErrorValidResponse();
		errorResponse.setTimestamp(Instant.now().toString());
		errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
		errorResponse.setErrors(errorInfos);

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
}
