package com.gabs.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabs.jwt.domain.AppUser;

public interface AppUserRepos extends JpaRepository<AppUser, Long>{
	//Pelo nome do método, o spring reconhece sua função
	AppUser findByUsername(String username);
}
