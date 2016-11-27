package com.govac.institutii.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.govac.institutii.db.User;
import com.govac.institutii.db.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	 @Autowired
	 private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String usrname) throws UsernameNotFoundException {
		Optional<User> usr = userRepository.findByEmail(usrname);
		return usr
			.map((u) -> {return new JwtUser(u);})
			.orElse(null);
	}

}
