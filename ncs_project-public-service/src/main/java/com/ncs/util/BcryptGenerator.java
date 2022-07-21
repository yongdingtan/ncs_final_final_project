package com.ncs.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BcryptGenerator {

	public String passwordEncoder(String password) {

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		return hashedPassword;
	}

	public Boolean passwordDecoder(String currentPassword, String ExistingPassword) {

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (passwordEncoder.matches(currentPassword, ExistingPassword)) {
			return true;
		} else {
			return false;
		}
	}
}