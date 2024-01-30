package net.sparkminds.library.service.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import net.sparkminds.library.service.validator.impl.EnumValueValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class)
public @interface ValidateEnumValue {
    String message() default "Invalid enum value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<?>> enumClass(); // Specify the enum class
}
