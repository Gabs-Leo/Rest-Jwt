package com.gabs.jwt;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gabs.jwt.domain.AppRole;
import com.gabs.jwt.domain.AppUser;
import com.gabs.jwt.services.AppUserService;

@SpringBootApplication
public class SpringJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJwtApplication.class, args);
	}
	
	@Bean
	CommandLineRunner run(AppUserService userService) {
		return args -> {
			userService.saveRole(new AppRole(null, "ROLE_USER"));
			userService.saveRole(new AppRole(null, "ROLE_MANAGER"));
			userService.saveRole(new AppRole(null, "ROLE_ADMIN"));
			userService.saveRole(new AppRole(null, "ROLE_SUPER_ADMIN"));
			
			userService.saveAppUser(new AppUser(null, "Gabriel de Morais", "gabs", "pass", new ArrayList<>()));
			userService.assignRole("gabs", "ROLE_SUPER_ADMIN");
			userService.saveAppUser(new AppUser(null, "Aylon Carrijo", "syllient", "pass", new ArrayList<>()));
			userService.assignRole("syllient", "ROLE_USER");
			
		};
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
}
