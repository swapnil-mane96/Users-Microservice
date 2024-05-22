package com.photoapp.users.entity;

import java.io.Serializable;
import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authorities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityEntity implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 3140925283922037028L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String authorityName;
	
	@ManyToMany(mappedBy = "authorities")
	private Collection<RoleEntity> roles;

	public AuthorityEntity(String authorityName) {
		this.authorityName = authorityName;
	}

	
	
}
