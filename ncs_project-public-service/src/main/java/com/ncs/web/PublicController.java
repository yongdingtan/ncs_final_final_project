package com.ncs.web;

import javax.validation.Valid;

import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ncs.dto.MessageResponse;
import com.ncs.dto.UserInfoResponse;
import com.ncs.exception.InvalidPasswordException;
import com.ncs.model.LoginRequest;
import com.ncs.model.MyUserDetails;
import com.ncs.model.SignupRequest;
import com.ncs.model.User;
import com.ncs.service.UserService;
import com.ncs.util.JWTUtil;

@Validated
@RestController
@CrossOrigin(origins = { "http://localhost:8092", "http://localhost:4200" }, allowedHeaders = "*")
@RequestMapping("/public")
public class PublicController {

	@Autowired
	private AuthenticationManager authenticationManager;
	UserService userService;
	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	PasswordValidator passwordValidator;
	@Autowired
	PasswordEncoder encoder;

	private static final Logger logger = LoggerFactory.getLogger(User.class);

	@Autowired
	public PublicController(UserService userService) {
		System.out.println("Inside Public Rest Controller Constructor");
		this.userService = userService;
	}

	// Create User
	// Takes in a SignupRequest body and checks with the database if the
	// username/email already exists
	// Returns a message stating success if successful

	@PostMapping("/register")
	@ResponseBody
	public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest u) throws Exception {
		logger.info("Inside User Creation");

		if (!PasswordValidator.isValid(u.getPassword()))
			throw new InvalidPasswordException(
					"Password must contain at least one digit [0-9]."
							+ "Password must contain at least one lowercase Latin character [a-z]."
							+ "Password must contain at least one uppercase Latin character [A-Z]."
							+ "Password must contain at least one special character like ! @ # & ( )."
							+ "Password must contain a length of at least 8 characters and a maximum of 20 characters.",
					u.getPassword(), 0);

		if (userService.findUserByUsername(u.getUsername()) != null) {
			throw new Exception("User already Exists");
		} else if (userService.findUserByEmail(u.getUsername()) != null) {
			throw new Exception("Email already exists");

		} else

		{
			User user = new User();
			user.setUsername(u.getUsername());
			user.setPassword(encoder.encode(u.getPassword()));
			user.setEmail(u.getEmail());
			user.setRole(u.getRole());
			userService.saveUser(user);
			return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
		}
	}

	// Login

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest u) throws InvalidCredentialsException {

		logger.info("Inside User Login");

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		ResponseCookie jwtCookie = jwtUtil.generateJwtCookie(userDetails);

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(new UserInfoResponse(
				userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getRole()));
	}

	// LogOut

	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtil.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!"));
	}

}
