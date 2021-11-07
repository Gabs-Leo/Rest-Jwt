package com.gabs.jwt.controllers;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@GetMapping("/token/refresh")
	public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException{
		String authorHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(authorHeader != null && authorHeader.startsWith("Bearer ")) {
			try {
				String refreshToken = authorHeader.substring("Bearer ".length());
				Algorithm alg = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(alg).build();
				DecodedJWT decodedJWT = verifier.verify(refreshToken);	
				String username = decodedJWT.getSubject();
				AppUser user = userService.getUserByUsername(username);
				String accessToken = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(AppRole::getName).collect(Collectors.toList()))
						.sign(alg);
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", accessToken);
				tokens.put("refresh_token", refreshToken);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			}catch(Exception e) {
				response.setHeader("error", e.getMessage());
				response.setStatus(FORBIDDEN.value());
				//response.sendError(FORBIDDEN.value());
				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh Token is Missing!");
		}
		return ResponseEntity.ok().build();
	}
}
