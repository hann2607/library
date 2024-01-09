package net.sparkminds.library.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldErrorInfo {
	private String field;
	private String message;
	private String errorCode;
}
