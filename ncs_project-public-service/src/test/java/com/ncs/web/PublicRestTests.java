package com.ncs.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ncs.dto.JWTResponseDTO;
import com.ncs.model.User;
import com.ncs.service.UserService;

class PublicRestTests {

	private UserService userService;

	@BeforeEach
	void setup() {
		userService = mock(UserService.class);
	}

	@Test
	void testCreateUser() {
		PublicController controller = new PublicController(userService);
		User user = new User(8, "test", "test", "test", "test", true, null, null, null);
		ResponseEntity<?> response = controller.createUser(user);

		assertEquals(new ResponseEntity<>("User successfully saved in the database", HttpStatus.OK), response);

	}

	@Test
	void testLoginUser() throws Exception {
		PublicController controller = new PublicController(userService);
		User user = new User(1, "test", "test", "test", "test", true, null, null, null);
		user.setUsername("Student G");
		user.setPassword("Password G");
		try {

			ResponseEntity<JWTResponseDTO> response = controller.loginUser(user);
			assertEquals(new ResponseEntity<>("Invalid Credentials", HttpStatus.OK), response);
		} catch (Exception e) {
			String expectedMessage = "Invalid credentials";
			assertEquals(expectedMessage, e.getMessage());
		}

	}

}
