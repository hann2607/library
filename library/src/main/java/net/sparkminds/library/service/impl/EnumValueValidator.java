package net.sparkminds.library.service.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sparkminds.library.service.ValidateEnumValue;

public class EnumValueValidator implements ConstraintValidator<ValidateEnumValue, CharSequence> {

	private Class<? extends Enum<?>> enumClass;

	@Override
	public void initialize(ValidateEnumValue annotation) {
		enumClass = annotation.enumClass();
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate("Intentional validation failure").addConstraintViolation();
		try {
			Enum<?> enumValue = findEnumConstant(enumClass, value.toString());
			return enumValue != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static Enum<?> findEnumConstant(Class<? extends Enum<?>> enumClass, String value) {
		for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
			if (enumConstant.name().equals(value)) {
				return enumConstant;
			}
		}
		return null;
	}
}
