package net.sparkminds.library.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.sparkminds.library.service.impl.DateTimeValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeValidator.class)
@Documented
public @interface ValidDateTime {
    String message() default "Invalid date format. Use yyyy-MM-dd for LocalDate and yyyy-MM-dd'T'HH:mm:ss for LocalDateTime";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}