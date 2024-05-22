package com.photoapp.users.entity;

import java.io.Serializable;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1809606847322649328L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String roleName;
	
	@ManyToMany(mappedBy = "roles")
	private Collection<Users> users;
	
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinTable(name = "roles_authorities", joinColumns=@JoinColumn(name="roles_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "authorities_id", referencedColumnName = "id"))
	private Collection<AuthorityEntity> authorities;

	public RoleEntity(String roleName, Collection<AuthorityEntity> authorities) {
		this.roleName = roleName;
		this.authorities = authorities;
	}
	
	
	
}
