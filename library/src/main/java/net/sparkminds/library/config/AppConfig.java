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
		messageSource.setBasenames("classpath:i18n/validation", "classpath:i18n/MessageError",
				"classpath:i18n/MessageSuccess");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	@Bean
	AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}
}
