package com.gabs.jwt.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gabs.jwt.domain.AppRole;
import com.gabs.jwt.domain.AppUser;
import com.gabs.jwt.domain.RoleToUser;
import com.gabs.jwt.services.AppUserServiceImpl;

@RestController
@RequestMapping("/api")
public class AppUserController {
	@Autowired
	AppUserServiceImpl userService;
	
	@GetMapping("/users")
	public ResponseEntity<Page<AppUser>> getUsers(Pageable pageable){
		return ResponseEntity.ok().body(userService.getUsersByPage(pageable));
	}
	
	@PostMapping("/users/process")
	public ResponseEntity<AppUser>saveUser(@RequestBody AppUser user){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/users/process").toUriString());
		return ResponseEntity.created(uri).body(userService.saveAppUser(user));
	}
	
	@PostMapping("/roles/process")
	public ResponseEntity<AppRole> saveRole(@RequestBody AppRole role){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/roles/process").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	
	//O <?> marca o retorno da response entity como void
	@PostMapping("/roles/attach")
	public ResponseEntity<?> attachRole(@RequestBody RoleToUser form){
		userService.assignRole(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
}
