package com.comwith.fitlog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FitlogApplication {

	public static void main(String[] args) {
		// .env 파일 로드
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach((entry) -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(FitlogApplication.class, args);
	}



//	@Bean
//	public BCryptPasswordEncoder bCryptPasswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
}
