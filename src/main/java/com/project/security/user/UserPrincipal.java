package com.project.security.user;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.project.entity.CustomerEntity;

public class UserPrincipal implements UserDetails { 

	private static final long serialVersionUID = 1L;
	private final CustomerEntity user;

	public UserPrincipal(CustomerEntity user) {
		this.user = user;
	}

	public Long getId() {
		return user.getId();
	}

	public String getRole() {
		return user.getRole().name();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled(); 
	}

}
