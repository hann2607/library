package net.sparkminds.library.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.sparkminds.library.service.impl.PhoneNumberValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface ValidPhoneNumber {
	String message() default "{phone.invalid}";
	
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    String countryCode();
}
