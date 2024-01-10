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

import net.sparkminds.library.dto.error.ErrorAPIResponse;
import net.sparkminds.library.dto.error.ErrorValidResponse;
import net.sparkminds.library.dto.error.FieldErrorInfo;

@ControllerAdvice
public class GlobalExceptionHandler {

	// Error for API Response
	@ExceptionHandler(RequestException.class)
	public static ResponseEntity<Object> handleValidationException(RequestException e) {
		ErrorAPIResponse errorAPIResponse = new ErrorAPIResponse();
		List<FieldErrorInfo> errorInfos = new ArrayList<>();
		
		FieldErrorInfo errorInfo = new FieldErrorInfo();
		errorInfo.setMessage(e.getMessage());
		errorInfos.add(errorInfo);
		
		errorAPIResponse.setTimestamp(Instant.now().toString());
		errorAPIResponse.setStatus(HttpStatus.BAD_REQUEST.value());
		errorAPIResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
		errorAPIResponse.setMessage(e.getMessage());
		
		return new ResponseEntity<>(errorAPIResponse, HttpStatus.BAD_REQUEST);
	}
	
	// Error for validation 
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public static ResponseEntity<ErrorValidResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        List<FieldErrorInfo> errorInfos = new ArrayList<>();
        for (FieldError error : errors) {
            String field = error.getField();
            String message = error.getDefaultMessage();
            FieldErrorInfo errorInfo = new FieldErrorInfo();
            errorInfo.setErrorCode(error.getObjectName() + "." + error.getField() +".invalid");
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
