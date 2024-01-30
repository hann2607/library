package net.sparkminds.library.service.validator.impl;

import java.time.format.DateTimeFormatter;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sparkminds.library.service.validator.ValidDateTime;

public class DateTimeValidator implements ConstraintValidator<ValidDateTime, CharSequence> {

    @Override
    public void initialize(ValidDateTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle this case
        }

        try {
            DateTimeFormatter.ISO_LOCAL_DATE.parse(value);
            return true;
        } catch (Exception e) {
            try {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
