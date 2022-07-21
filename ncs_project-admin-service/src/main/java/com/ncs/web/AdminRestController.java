package com.ncs.web;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ncs.dto.QuestionResponseDTO;
import com.ncs.dto.TestScoreResponseDTO;
import com.ncs.dto.UserResponseDTO;
import com.ncs.exception.InvalidCorrectAnswerException;
import com.ncs.exception.ResourceNotFoundException;
import com.ncs.model.Question;
import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.service.QuestionService;
import com.ncs.service.TestScoreService;
import com.ncs.service.UserService;
import com.ncs.util.QuestionDTOConversion;
import com.ncs.util.TestScoreDTOConversion;
import com.ncs.util.UserDTOConversion;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

	UserService userService;
	TestScoreService testScoreService;
	QuestionService questionService;
	@Autowired
	RestTemplate restTemplate;

	TestScoreDTOConversion dtoTestScore;
	UserDTOConversion dtoUser;
	QuestionDTOConversion dtoQuestion;

	private static final Logger loggerUser = LoggerFactory.getLogger(User.class);
	private static final Logger loggerTestScore = LoggerFactory.getLogger(Test_Score.class);
	private static final Logger loggerQuestion = LoggerFactory.getLogger(Question.class);

	@Autowired
	public AdminRestController(UserService userService, TestScoreService testScoreService,
			QuestionService questionService) {
		System.out.println("Inside Admin Rest Controller Constructor");
		this.userService = userService;
		this.testScoreService = testScoreService;
		this.questionService = questionService;
	}

	// Read User By UserID
	@GetMapping("/read/{id}")
	@ResponseBody
	public ResponseEntity<UserResponseDTO> getUserDetails(@RequestHeader(name = "Authorization") String token,
			@PathVariable int id) throws Exception {
		loggerUser.info("Inside Get User API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			User userExists = userService.findUserById(id);
			if (userExists == null) {
				throw new ResourceNotFoundException("User with ID: " + id + " not found", "Id", id);
			} else {
				UserResponseDTO user = dtoUser.convertToResponse(userExists);
				return new ResponseEntity<UserResponseDTO>(user, HttpStatus.OK);
			}
		} else {
			throw new Exception("Invalid credentials");
		}

	}

	// Get all User By Roles
	@GetMapping("/roles/{roles}")
	@ResponseBody
	public ResponseEntity<List<UserResponseDTO>> getUsersByRoles(@RequestHeader(name = "Authorization") String token,
			@PathVariable String roles) throws Exception {
		loggerUser.info("Inside Get User By Roles API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			List<User> allUsers = userService.getAllUsersByRole(roles);
			if (allUsers.isEmpty()) {
				throw new ResourceNotFoundException("No Users Found", roles, 0);
			} else {
				List<UserResponseDTO> userResponse = new ArrayList<>();
				for (User user : allUsers) {
					userResponse.add(dtoUser.convertToRoleResponse(user));
				}
				return new ResponseEntity<List<UserResponseDTO>>(userResponse, HttpStatus.OK);
			}
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Update User
	@PutMapping("/user/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editUser(@RequestHeader(name = "Authorization") String token, @PathVariable int id,
			@RequestBody User u) throws Exception {
		loggerUser.info("Inside Edit User API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			User userExists = userService.findUserById(id);
			if (userExists == null) {
				throw new ResourceNotFoundException("Admin with ID: " + id + " not found", "Id", id);
			} else {
				userService.editUser(u);
			}

			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Delete User
	@PutMapping("/user/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteUser(@RequestHeader(name = "Authorization") String token, @PathVariable int id)
			throws Exception {
		loggerUser.info("Inside Delete User API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			User userExists = userService.findUserById(id);
			if (userExists == null) {
				throw new ResourceNotFoundException("User", "Id", id);
			} else {
				boolean status = userService.deleteUser(id);
				if (status)
					return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
				else
					return new ResponseEntity<>("User deletion failed", HttpStatus.BAD_REQUEST);
			}
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Get Test Score By ID
	@GetMapping("/test_score/{id}")
	@ResponseBody
	public ResponseEntity<TestScoreResponseDTO> readTestScore(@RequestHeader(name = "Authorization") String token,
			@PathVariable int id) throws Exception {
		loggerTestScore.info("Inside Get Test Score API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			Test_Score ts = testScoreService.readTestScore(id);
			TestScoreResponseDTO testScore = dtoTestScore.convertToResponse(ts);
			return new ResponseEntity<TestScoreResponseDTO>(testScore, HttpStatus.OK);

		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Get all Test Scores By User ID
	@GetMapping("/test_score/{userId}")
	@ResponseBody
	public List<TestScoreResponseDTO> readAllTestScoreByUserID(@RequestHeader(name = "Authorization") String token,
			@RequestParam int userId) throws Exception {
		loggerTestScore.info("Inside Get All Test Scores By User ID API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {

			List<Test_Score> rawTestScores = testScoreService.readAllTestScoreByUserID(userId);
			List<TestScoreResponseDTO> allTestScores = new ArrayList<>();
			for (Test_Score testScore : rawTestScores) {
				allTestScores.add(dtoTestScore.convertToResponse(testScore));
			}
			return allTestScores;
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Edit Test Score By ID
	@PutMapping("/test_score/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editTestScore(@RequestHeader(name = "Authorization") String token, @PathVariable int id,
			@RequestBody Test_Score ts) throws Exception {
		loggerTestScore.info("Inside Edit Test Score API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			Test_Score tsExists = testScoreService.readTestScore(id);
			if (tsExists == null) {
				throw new ResourceNotFoundException("Test Score with ID " + id + " not found", "Id", id);
			} else {
				testScoreService.editTestScore(ts);
			}
			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Delete Test Score By ID
	@DeleteMapping("/test_score/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteTestScore(@RequestHeader(name = "Authorization") String token, @PathVariable int id)
			throws Exception {
		loggerTestScore.info("Inside Delete Test Score API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			Test_Score tsExists = testScoreService.readTestScore(id);
			if (tsExists == null) {
				throw new ResourceNotFoundException("Test Score with ID " + id + " not found", "Id", id);
			} else {
				boolean status = testScoreService.deleteTestScore(id);
				if (status)
					return new ResponseEntity<>("Test Score deleted successfully", HttpStatus.OK);
				else
					return new ResponseEntity<>("Test Score deletion failed", HttpStatus.BAD_REQUEST);
			}
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Create Question
	@PostMapping("/question/create")
	@ResponseBody
	public ResponseEntity<QuestionResponseDTO> createQuestion(@RequestHeader(name = "Authorization") String token,
			@Valid @RequestBody Question q) throws Exception {
		loggerQuestion.info("Inside Question Creation");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			List<String> allOptions = new ArrayList<>();
			allOptions.add(q.getQuestionOptionOne());
			allOptions.add(q.getQuestionOptionTwo());
			allOptions.add(q.getQuestionOptionThree());
			allOptions.add(q.getQuestionOptionFour());

			if (allOptions.contains(q.getCorrectAnswer())) {

				Question savedQuestion = questionService.createQuestion(q);
				QuestionResponseDTO questionResponse = dtoQuestion.convertToResponse(savedQuestion);
				return new ResponseEntity<QuestionResponseDTO>(questionResponse, HttpStatus.OK);
			} else
				throw new InvalidCorrectAnswerException("Options does not contain correct answer", q.getCorrectAnswer(),
						q.getQuestionNumber());
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Get Question By ID
	@GetMapping("/question/{id}")
	@ResponseBody
	public ResponseEntity<QuestionResponseDTO> readQuestion(@RequestHeader(name = "Authorization") String token,
			@PathVariable int id) throws Exception {
		loggerQuestion.info("Inside Get Question API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			Question question = questionService.readQuestion(id);
			if (question == null) {
				throw new ResourceNotFoundException("Question ID not found", "Question Id", id);
			} else {
				QuestionResponseDTO questionResponse = dtoQuestion.convertToResponse(question);
				return new ResponseEntity<QuestionResponseDTO>(questionResponse, HttpStatus.OK);
			}
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Get All Questions By Category
	@GetMapping("/question/category")
	@ResponseBody
	public List<Question> getAllQuestionByCategory(@RequestHeader(name = "Authorization") String token,
			@RequestParam String category) throws Exception {
		loggerQuestion.info("Inside Get Category API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {

			List<Question> listOfQuestions = questionService.getAllQuestionByCategory(category);
			if (listOfQuestions.isEmpty())
				throw new ResourceNotFoundException("Questions in " + category + " not found ", "Category", 0);
			else
				return listOfQuestions;
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Update Question By ID
	@PutMapping("/question/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editQuestion(@RequestHeader(name = "Authorization") String token, @PathVariable int id,
			@RequestBody Question q) throws Exception {
		loggerQuestion.info("Inside Edit Question API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			Question questionExists = questionService.readQuestion(id);
			if (questionExists == null)
				throw new ResourceNotFoundException("Question " + id + " not found", "Id", id);
			else {
				questionService.editQuestion(q);
			}

			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Deletes a Question By ID
	@DeleteMapping("/question/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteQuestion(@RequestHeader(name = "Authorization") String token, @PathVariable int id)
			throws Exception {
		loggerQuestion.info("Inside Delete Question API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			Question questionExists = questionService.readQuestion(id);
			if (questionExists == null)
				throw new ResourceNotFoundException("Question " + id + " not found", "Id", id);
			else {
				boolean status = questionService.deleteQuestion(id);
				if (status)
					return new ResponseEntity<>("Question deleted successfully", HttpStatus.OK);
				else
					return new ResponseEntity<>("Question deletion failed", HttpStatus.BAD_REQUEST);
			}
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

}
