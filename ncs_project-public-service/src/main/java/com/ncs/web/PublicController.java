package com.ncs.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ncs.dto.JWTResponseDTO;
import com.ncs.model.User;
import com.ncs.service.UserService;
import com.ncs.service.UserServiceImpl;
import com.ncs.util.JWTUtil;

@RestController
@RequestMapping("/public")
public class PublicController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserServiceImpl userServiceImpl;
	UserService userService;
	@Autowired
	JWTUtil jwtUtil;

	private static final Logger logger = LoggerFactory.getLogger(User.class);

	@Autowired
	public PublicController(UserService userService) {
		System.out.println("Inside Public Rest Controller Constructor");
		this.userService = userService;
	}

	// Create User
	@PostMapping("/register")
	@ResponseBody
	public ResponseEntity<?> createUser(@RequestBody User u) {
		logger.info("Inside User Creation");

		User userExists = userService.findUserByUsername(u.getUsername());
		User emailExists = userService.findUserByEmail(u.getUsername());

		if (userExists != null) {
			return new ResponseEntity<>("There is already a user registered with the username provided", HttpStatus.OK);
		} else if (emailExists != null) {
			return new ResponseEntity<>("There is already a user registered with the email provided", HttpStatus.OK);

		} else

		{
			userService.saveUser(u);
			return new ResponseEntity<>("User successfully saved in the database", HttpStatus.OK);
		}
	}

	// Login
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<JWTResponseDTO> loginUser(@RequestBody User u) throws Exception {

		logger.info("Inside User Login");

		try {

			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword()));

		} catch (Exception e) {
			throw new Exception("Invalid credentials");
		}

		UserDetails userDetails = userServiceImpl.loadUserByUsername(u.getUsername());

		String token = jwtUtil.generateToken(userDetails);

		boolean isValid = token != null ? true : false;

		JWTResponseDTO jwtResponseDTO = new JWTResponseDTO(token, u.getUsername(), isValid);

		return new ResponseEntity<JWTResponseDTO>(jwtResponseDTO, HttpStatus.OK);
	}

}
