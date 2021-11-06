package com.gabs.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabs.jwt.domain.AppRole;

public interface AppRoleRepos extends JpaRepository<AppRole, Long>{
	AppRole findByName(String name);
}
