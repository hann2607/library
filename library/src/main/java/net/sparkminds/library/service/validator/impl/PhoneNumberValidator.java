package net.sparkminds.library.service.validator.impl;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.sparkminds.library.service.validator.ValidPhoneNumber;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

	private String countryCode;

	@Override
	public void initialize(ValidPhoneNumber constraintAnnotation) {
		this.countryCode = constraintAnnotation.countryCode();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return true;        // Accept phone null
		}
		try {
			PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
			Phonenumber.PhoneNumber number = phoneNumberUtil.parse(value, countryCode);
			return phoneNumberUtil.isValidNumber(number);
		} catch (Exception e) {
			return false;
		}
	}
}
