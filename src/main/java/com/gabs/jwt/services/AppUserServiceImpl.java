package com.gabs.jwt.services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gabs.jwt.domain.AppRole;
import com.gabs.jwt.domain.AppUser;
import com.gabs.jwt.repositories.AppRoleRepos;
import com.gabs.jwt.repositories.AppUserRepos;

@Service
@Transactional
public class AppUserServiceImpl implements AppUserService, UserDetailsService{

	@Autowired
	private final AppRoleRepos roleRepos;
	
	@Autowired
	private final AppUserRepos userRepos;
	
	public AppUserServiceImpl(AppRoleRepos roleRepos, AppUserRepos userRepos) {
		super();
		this.roleRepos = roleRepos;
		this.userRepos = userRepos;
	}

	@Override
	public AppUser saveAppUser(AppUser user) {
		System.out.println("Salvando o Usuário " + user.getName() + "!");
		return userRepos.save(user);
	}

	@Override
	public AppRole saveRole(AppRole role) {
		System.out.println("Salvando Classe " + role.getName() + "!");
		return roleRepos.save(role);
	}

	@Override
	public void assignRole(String username, String roleName) {
		AppUser user = userRepos.findByUsername(username);
		AppRole role = roleRepos.findByName(roleName);
		System.out.println("Associando Classe " + roleName + " ao usuário " + username + "!");
		user.getRoles().add(role);
	}

	@Override
	public AppUser getUserByUsername(String username) {
		System.out.println("Recebendo o usuário com o username " + username);
		return userRepos.findByUsername(username);
	}

	@Override
	public Page<AppUser> getUsersByPage(Pageable pageable) {
		System.out.println("Adicionando paginações!");
		userRepos.findAll();
		Page<AppUser> result = userRepos.findAll(pageable);
		return result.map(i -> new AppUser(i.getId(), i.getName(), i.getUsername(), i.getPassword(), i.getRoles()));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userRepos.findByUsername(username);
		if(user == null) {
			System.out.println("Usuário não encontrado!");
			throw new UsernameNotFoundException("Usuário não encontrado!");
		}else {
			System.out.println("User " + username + " encontrado!");
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(i ->
			authorities.add(new SimpleGrantedAuthority(i.getName())));
		return new User(user.getUsername(), user.getPassword(), authorities);
	}

}
