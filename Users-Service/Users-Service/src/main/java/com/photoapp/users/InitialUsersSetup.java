package com.photoapp.users;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.photoapp.users.entity.AuthorityEntity;
import com.photoapp.users.entity.RoleEntity;
import com.photoapp.users.entity.Users;
import com.photoapp.users.enums.Roles;
import com.photoapp.users.repositories.AuthorityRepository;
import com.photoapp.users.repositories.RolesRepository;
import com.photoapp.users.repositories.UsersRepository;

import jakarta.transaction.Transactional;

@Component
public class InitialUsersSetup {
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RolesRepository rolesRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UsersRepository usersRepository;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Transactional
	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		this.logger.info("From application ready event...");
		AuthorityEntity readAuthority = createAuthority("READ");
		AuthorityEntity writeAuthority = createAuthority("WRITE");
		AuthorityEntity deleteAuthority = createAuthority("DELETE");

		createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority,deleteAuthority));
		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
		if (roleAdmin == null)
			return;
		
		Users users = new Users();
		users.setPublicUserId(UUID.randomUUID().toString());
		users.setFirstName("Swapnil");
		users.setLastName("Mane");
		users.setEmail("admin@test.com");
		users.setEncryptedPassword(this.bCryptPasswordEncoder.encode("1234"));
		users.setRoles(Arrays.asList(roleAdmin));
		
		Users storedAdminUser = this.usersRepository.findByEmail("admin@test.com");
		if (storedAdminUser == null) {
			this.usersRepository.save(users);
		}
	}
	
	@Transactional
	private AuthorityEntity createAuthority(String authorityName) {
		AuthorityEntity authorityEntity = this.authorityRepository.findByAuthorityName(authorityName);
		if (authorityEntity == null) {
			authorityEntity = new AuthorityEntity(authorityName);
			this.authorityRepository.save(authorityEntity);
		}
		return authorityEntity;
	}
	
	@Transactional
	private RoleEntity createRole(String roleName, Collection<AuthorityEntity> authorities) {
		RoleEntity roleEntity = this.rolesRepository.findByRoleName(roleName);
		if(roleEntity == null) {
			roleEntity = new RoleEntity(roleName, authorities);
			this.rolesRepository.save(roleEntity);
		}
		return roleEntity;
	}
	
	
	
}
