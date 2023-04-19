package com.app.IVAS;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableJpaAuditing
@EnableJpaRepositories
@EnableScheduling
@EnableCaching
public class IvasApplication extends SpringBootServletInitializer {

	public static void main(String[] args) { SpringApplication.run(IvasApplication.class, args); }

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(IvasApplication.class);
	}

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			log.info("------------------*---------------------");
			log.info("|                                      |");
			log.info("|  Started IVAS API Application        |");
			log.info("|                                      |");
			log.info("------------------*---------------------");
		};
	}

}
