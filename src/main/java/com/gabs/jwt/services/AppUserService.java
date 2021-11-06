package com.gabs.jwt.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gabs.jwt.domain.AppRole;
import com.gabs.jwt.domain.AppUser;

public interface AppUserService {
	AppUser saveAppUser(AppUser user);
	AppRole saveRole(AppRole role);
	void assignRole(String username, String roleName);
	
	AppUser getUserByUsername(String username);
	Page<AppUser> getUsersByPage(Pageable pageable);
}
