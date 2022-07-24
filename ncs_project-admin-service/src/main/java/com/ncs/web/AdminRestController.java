package com.ncs.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
import com.ncs.dto.StudentAverageTestScoreResponseDTO;
import com.ncs.dto.TestScoreResponseDTO;
import com.ncs.dto.UserResponseDTO;
import com.ncs.dto.UserTestScoreResponseDTO;
import com.ncs.exception.InvalidCorrectAnswerException;
import com.ncs.exception.InvalidCredentialsException;
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

	public Boolean validateUser(String token) {
		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "admin");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		return jwtStatus;
	}

	// Get all User By Roles
	@GetMapping("/user/roles/{roles}")
	@ResponseBody
	public ResponseEntity<List<UserResponseDTO>> getUsersByRoles(@RequestHeader(name = "Authorization") String token,
			@PathVariable String roles) throws Exception {
		loggerUser.info("Inside Get User By Roles API Call");

		if (validateUser(token)) {
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
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Read User By UserID
	@GetMapping("/user/read/{id}")
	@ResponseBody
	public ResponseEntity<UserResponseDTO> getUserDetails(@RequestHeader(name = "Authorization") String token,
			@PathVariable int id) throws Exception {
		loggerUser.info("Inside Get User API Call");

		if (validateUser(token)) {
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

	// Update User
	@PutMapping("/user/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editUser(@RequestHeader(name = "Authorization") String token, @PathVariable int id,
			@RequestBody User u) throws Exception {
		loggerUser.info("Inside Edit User API Call");

		if (validateUser(token)) {
			User userExists = userService.findUserById(id);
			if (userExists == null) {
				throw new ResourceNotFoundException("User with ID: " + id + " not found", "Id", id);
			} else if (userExists.getRole().equalsIgnoreCase("admin")) {
				throw new Exception("Admin cannot edit another admin credentials");
			} else {
				userService.editUser(userExists, u);
				return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);
			}

		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Delete User
	@DeleteMapping("/user/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteUser(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int id) throws Exception {
		loggerUser.info("Inside Delete User API Call");

		if (validateUser(token)) {
			User userExists = userService.findUserById(id);
			if (userExists == null) {
				throw new ResourceNotFoundException("User", "Id", id);
			} else if (userExists.getRole() == "admin") {
				throw new Exception("Admin cannot delete another admin");
			} else {
				Set<Test_Score> tsExists = userExists.getAllTestScore();
				if (tsExists.isEmpty()) {
					boolean status = userService.deleteUser(id);
					if (status)
						return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
					else
						return new ResponseEntity<>("User deletion failed", HttpStatus.BAD_REQUEST);
				} else {
					throw new Exception("Cannot delete student who still has test score records");
				}

			}
		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Get Test Score By ID
	@GetMapping("/testscore/{id}")
	@ResponseBody
	public ResponseEntity<TestScoreResponseDTO> readTestScore(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int testId) throws Exception {
		loggerTestScore.info("Inside Get Test Score API Call");

		if (validateUser(token)) {
			Test_Score ts = testScoreService.getTestScoreByID(testId);
			TestScoreResponseDTO testScore = dtoTestScore.convertToResponse(ts);
			return new ResponseEntity<TestScoreResponseDTO>(testScore, HttpStatus.OK);

		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Get all Test Scores By User ID
	@GetMapping("/testscore/user/{userId}")
	@ResponseBody
	public List<TestScoreResponseDTO> readAllTestScoreByUserID(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int userId) throws Exception {
		loggerTestScore.info("Inside Get All Test Scores By User ID API Call");

		if (validateUser(token)) {
			User user = userService.findUserById(userId);
			Set<Test_Score> rawTestScores = user.getAllTestScore();
			List<TestScoreResponseDTO> allTestScores = new ArrayList<>();
			for (Test_Score testScore : rawTestScores) {
				allTestScores.add(dtoTestScore.convertToResponse(testScore));
			}
			return allTestScores;
		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Edit Test Score By ID
	@PutMapping("/testscore/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editTestScore(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int id, @RequestBody Test_Score ts) throws Exception {
		loggerTestScore.info("Inside Edit Test Score API Call");

		if (validateUser(token)) {
			Test_Score tsExists = testScoreService.getTestScoreByID(id);
			if (tsExists == null) {
				throw new ResourceNotFoundException("Test Score with ID " + id + " not found", "Id:", id);
			} else {
				testScoreService.editTestScore(ts, id);
			}
			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Delete Test Score By ID
	@DeleteMapping("/testscore/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteTestScore(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int id) throws Exception {
		loggerTestScore.info("Inside Delete Test Score API Call");

		if (validateUser(token)) {
			Set<Test_Score> tsExists = testScoreService.readTestScore(id);
			if (tsExists.isEmpty()) {
				throw new ResourceNotFoundException("Test Score with ID " + id + " not found", "Id", id);
			} else {
				boolean status = testScoreService.deleteTestScore(id);
				if (status)
					return new ResponseEntity<>("Test Score deleted successfully", HttpStatus.OK);
				else
					return new ResponseEntity<>("Test Score deletion failed", HttpStatus.BAD_REQUEST);
			}
		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Filters students by their average test score and returns a list from highest
	// score to lowest score
	@GetMapping("/student/filter")
	public ResponseEntity<List<StudentAverageTestScoreResponseDTO>> filterStudentsByAverageTestScore(
			@RequestHeader(name = "Authorization") String token, @RequestParam(required = false) String category,
			@RequestParam(required = false) String level) {
		loggerUser.info("Inside Filter Student GET API Call");

		if (validateUser(token)) {
			int count = 0;
			int totalMarks = 0;
			double average = 0;
			List<User> allStudents = userService.getAllStudents();
			List<StudentAverageTestScoreResponseDTO> responseDTO = new ArrayList<>();
			if (category == null && level == null) {
				for (User user : allStudents) {
					for (Test_Score ts : user.getAllTestScore()) {
						totalMarks += ts.getMarks();
						count++;
					}
					average = totalMarks / count;
					responseDTO.add(dtoUser.convertToAverageTestScoreResponse(user, (int) average));
					count = 0;
					totalMarks = 0;
					average = 0;
				}
			} else if (category != null && level == null) {
				for (User user : allStudents) {
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getCategory().equals(category)) {
							totalMarks += ts.getMarks();
							count++;
						}
					}
					average = totalMarks / count;
					responseDTO.add(dtoUser.convertToAverageTestScoreResponse(user, (int) average));
					count = 0;
					totalMarks = 0;
					average = 0;
				}
			} else if (category == null && level != null) {
				for (User user : allStudents) {
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getLevel().equals(level)) {
							totalMarks += ts.getMarks();
							count++;
						}
					}
					average = totalMarks / count;
					responseDTO.add(dtoUser.convertToAverageTestScoreResponse(user, (int) average));
					count = 0;
					totalMarks = 0;
					average = 0;
				}
			} else if (category != null && level != null) {
				for (User user : allStudents) {
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getLevel().equals(level) && ts.getCategory().equals(category)) {
							totalMarks += ts.getMarks();
							count++;
						}
					}
					average = totalMarks / count;
					responseDTO.add(dtoUser.convertToAverageTestScoreResponse(user, (int) average));
					count = 0;
					totalMarks = 0;
					average = 0;
				}
			}
			Collections.sort(responseDTO, new Comparator<StudentAverageTestScoreResponseDTO>() {
				public int compare(StudentAverageTestScoreResponseDTO a, StudentAverageTestScoreResponseDTO b) {
					return b.getAverageScore() - a.getAverageScore();
				}
			});
			return new ResponseEntity<List<StudentAverageTestScoreResponseDTO>>(responseDTO, HttpStatus.OK);

		} else {

			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}
	}

	// Sort students by their test category and returns a list
	@GetMapping("/student/sort")
	public ResponseEntity<List<UserTestScoreResponseDTO>> sortStudentsByCategory(
			@RequestHeader(name = "Authorization") String token, @RequestParam(required = true) String category,
			@RequestParam(required = false) String level) {
		loggerUser.info("Inside Filter Student GET API Call");

		if (validateUser(token)) {
			List<User> allStudents = userService.getAllStudents();
			List<UserTestScoreResponseDTO> response = new ArrayList<>();
			if (level == null) {
				for (User user : allStudents) {

					List<TestScoreResponseDTO> validTestScores = new ArrayList<>();
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getCategory().equals(category)) {
							validTestScores.add(dtoTestScore.convertToResponse(ts));
						}
					}
					response.add(dtoUser.convertToUserTestScoreResponse(user, validTestScores));
				}
			} else if (level != null) {
				for (User user : allStudents) {

					List<TestScoreResponseDTO> validTestScores = new ArrayList<>();
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getCategory().equals(category) && ts.getLevel().equals(level)) {
							validTestScores.add(dtoTestScore.convertToResponse(ts));
						}
					}
					response.add(dtoUser.convertToUserTestScoreResponse(user, validTestScores));
				}
			}

			return new ResponseEntity<List<UserTestScoreResponseDTO>>(response, HttpStatus.OK);

		} else {

			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}
	}

	// Create Question
	@PostMapping("/question/create")
	@ResponseBody
	public ResponseEntity<QuestionResponseDTO> createQuestion(@RequestHeader(name = "Authorization") String token,
			@Valid @RequestBody Question q) throws Exception {
		loggerQuestion.info("Inside Question Creation");

		if (validateUser(token)) {
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
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Get Question By ID
	@GetMapping("/question/{id}")
	@ResponseBody
	public ResponseEntity<QuestionResponseDTO> readQuestion(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int id) throws Exception {
		loggerQuestion.info("Inside Get Question API Call");

		if (validateUser(token)) {
			Question question = questionService.readQuestion(id);
			if (question == null) {
				throw new ResourceNotFoundException("Question ID not found", "Question Id", id);
			} else {
				QuestionResponseDTO questionResponse = dtoQuestion.convertToResponse(question);
				return new ResponseEntity<QuestionResponseDTO>(questionResponse, HttpStatus.OK);
			}
		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Get All Questions By Category
	@GetMapping("/question/category")
	@ResponseBody
	public ResponseEntity<List<QuestionResponseDTO>> getAllQuestionByCategory(
			@RequestHeader(name = "Authorization") String token, @RequestParam String category) throws Exception {
		loggerQuestion.info("Inside Get Category API Call");

		if (validateUser(token)) {

			List<Question> listOfQuestions = questionService.getAllQuestionByCategory(category);
			if (listOfQuestions.isEmpty())
				throw new ResourceNotFoundException("Questions in " + category + " not found ", "Category", 0);
			else {
				List<QuestionResponseDTO> responseDTO = new ArrayList<>();
				for (Question question : listOfQuestions) {
					responseDTO.add(dtoQuestion.convertToResponse(question));
				}
				return new ResponseEntity<List<QuestionResponseDTO>>(responseDTO, HttpStatus.OK);
			}
		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Update Question By ID
	@PutMapping("/question/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editQuestion(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int id, @RequestBody Question q) throws Exception {
		loggerQuestion.info("Inside Edit Question API Call");

		if (validateUser(token)) {
			Question questionExists = questionService.readQuestion(id);
			if (questionExists == null)
				throw new ResourceNotFoundException("Question " + id + " not found", "Id", id);
			else {
				questionService.editQuestion(q);
			}

			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

		} else {
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

	// Deletes a Question By ID
	@DeleteMapping("/question/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteQuestion(@RequestHeader(name = "Authorization") String token,
			@PathVariable(required = true) int id) throws Exception {
		loggerQuestion.info("Inside Delete Question API Call");

		if (validateUser(token)) {
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
			throw new InvalidCredentialsException("Invalid Credentials", null, 0);
		}

	}

}
