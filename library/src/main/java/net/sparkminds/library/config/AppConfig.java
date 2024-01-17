package net.sparkminds.library.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.sparkminds.library.service.impl.AuditorAwareImpl;

@Configuration
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class AppConfig implements WebMvcConfigurer {

	@Bean("messageSource")
	MessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:i18n/validationRegisterRequest", "classpath:i18n/validationAccount",
				"classpath:i18n/validationAdmin", "classpath:i18n/validationSession", "classpath:i18n/validationUser",
				"classpath:i18n/MessageError", "classpath:i18n/MessageSuccess",
				"classpath:i18n/validationRefreshRequestToken");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}
}
