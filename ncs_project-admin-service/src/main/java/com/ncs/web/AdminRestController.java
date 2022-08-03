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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.ncs.exception.ResourceNotFoundException;
import com.ncs.model.Question;
import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.repository.QuestionRepository;
import com.ncs.service.QuestionService;
import com.ncs.service.TestScoreService;
import com.ncs.service.UserService;
import com.ncs.util.QuestionDTOConversion;
import com.ncs.util.TestScoreDTOConversion;
import com.ncs.util.UserDTOConversion;

@RestController
@CrossOrigin(origins = { "http://localhost:8090", "http://localhost:4200" }, allowedHeaders = "*")
@RequestMapping("/admin")
public class AdminRestController {

	UserService userService;
	TestScoreService testScoreService;
	QuestionService questionService;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	QuestionRepository questionRepository;

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

	// Get all User By Roles
	// Returns a list of user which has that role
	@GetMapping("/user/roles/{roles}")
	@ResponseBody
	public ResponseEntity<List<UserResponseDTO>> getUsersByRoles(@PathVariable String roles) throws Exception {
		loggerUser.info("Inside Get User By Roles API Call");

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

	}

	// Read User By UserID
	// Returns the user who has that id
	@GetMapping("/user/read/{id}")
	@ResponseBody
	public ResponseEntity<UserResponseDTO> getUserDetails(@PathVariable int id) throws Exception {
		loggerUser.info("Inside Get User API Call");

		User userExists = userService.findUserById(id);
		if (userExists == null) {
			throw new ResourceNotFoundException("User with ID: " + id + " not found", "Id", id);
		} else {
			UserResponseDTO user = dtoUser.convertToResponse(userExists);
			return new ResponseEntity<UserResponseDTO>(user, HttpStatus.OK);
		}

	}

	// Update User
	// Checks if the new email is already in use
	// Returns a success message upon successful update
	@PutMapping("/user/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editUser(@PathVariable int id, @RequestBody User u) throws Exception {
		loggerUser.info("Inside Edit User API Call");

		User userExists = userService.findUserById(id);
		User emailExists = userService.findUserByEmail(u.getEmail());
		if (userExists == null) {
			throw new ResourceNotFoundException("User with ID: " + id + " not found", "Id", id);
		} else if (emailExists != null) {
			throw new Exception("Email already exists");
		} else {
			userService.editUser(userExists, u);
			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);
		}

	}

	// Delete User
	// Checks if the student still has test scores
	// Returns a success message upon successful deletion
	@DeleteMapping("/user/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteUser(@PathVariable(required = true) int id) throws Exception {
		loggerUser.info("Inside Delete User API Call");

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

	}

	@GetMapping("/results")
	@ResponseBody
	public ResponseEntity<List<TestScoreResponseDTO>> getAllResults() {
		List<User> allStudents = userService.getAllStudents();
		List<TestScoreResponseDTO> response = new ArrayList<>();
		for (User user : allStudents) {
			for (Test_Score ts : user.getAllTestScore()) {
				if (ts.isIs_available() == true)
					response.add(dtoTestScore.convertToResponseWithUserId(ts, user.getUserId()));
			}
		}

		return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);
	}

	// Get Test Score By ID
	// Returns the test score which has that ID
	@GetMapping("/testscore/{testId}")
	@ResponseBody
	public ResponseEntity<TestScoreResponseDTO> readTestScore(@PathVariable(required = true) int testId)
			throws Exception {
		loggerTestScore.info("Inside Get Test Score API Call");

		Test_Score ts = testScoreService.getTestScoreByID(testId);
		if (ts == null) {
			throw new ResourceNotFoundException("Test Score with ID: " + testId + " not found.", null, 0);
		} else {
			TestScoreResponseDTO testScore = dtoTestScore.convertToResponse(ts);
			return new ResponseEntity<TestScoreResponseDTO>(testScore, HttpStatus.OK);
		}

	}

	// Get all Test Scores By User ID
	@GetMapping("/testscore/user/{userId}")
	@ResponseBody
	public List<TestScoreResponseDTO> readAllTestScoreByUserID(@PathVariable(required = true) int userId)
			throws Exception {
		loggerTestScore.info("Inside Get All Test Scores By User ID API Call");

		User user = userService.findUserById(userId);
		Set<Test_Score> rawTestScores = user.getAllTestScore();
		List<TestScoreResponseDTO> allTestScores = new ArrayList<>();
		for (Test_Score testScore : rawTestScores) {
			allTestScores.add(dtoTestScore.convertToResponse(testScore));
		}
		return allTestScores;

	}

	@GetMapping("/testscore/read")
	public ResponseEntity<List<TestScoreResponseDTO>> getAllTestScoreBasedOnParams(
			@RequestParam(required = false) String studentid, @RequestParam(required = false) String category,
			@RequestParam(required = false) String level) {
		if (!studentid.equalsIgnoreCase("All Students")) {
			int id = Integer.parseInt(studentid);
			User user = userService.findUserById(id);
			Set<Test_Score> rawTestScores = user.getAllTestScore();
			List<TestScoreResponseDTO> response = new ArrayList<>();
			if (!category.equalsIgnoreCase("All Categories") && !level.equalsIgnoreCase("All levels")) {
				for (Test_Score test_Score : rawTestScores) {
					if (test_Score.getCategory().equalsIgnoreCase(category)
							&& test_Score.getLevel().equalsIgnoreCase(level)) {
						response.add(dtoTestScore.convertToResponseWithUserId(test_Score, id));
					}
				}
				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			} else if (category.equalsIgnoreCase("All Categories") && !level.equalsIgnoreCase("All levels")) {
				for (Test_Score test_Score : rawTestScores) {
					if (test_Score.getLevel().equalsIgnoreCase(level)) {
						response.add(dtoTestScore.convertToResponseWithUserId(test_Score, id));
					}
				}
				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			} else if (!category.equalsIgnoreCase("All Categories") && level.equalsIgnoreCase("All levels")) {
				for (Test_Score test_Score : rawTestScores) {
					if (test_Score.getCategory().equalsIgnoreCase(category)) {
						response.add(dtoTestScore.convertToResponseWithUserId(test_Score, id));
					}
				}
				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			} else {
				for (Test_Score test_Score : rawTestScores) {
					{
						response.add(dtoTestScore.convertToResponseWithUserId(test_Score, id));
					}
				}
				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			}
		} else {
			List<User> allStudents = userService.getAllStudents();
			if (!category.equalsIgnoreCase("All Categories") && !level.equalsIgnoreCase("All levels")) {
				List<TestScoreResponseDTO> response = new ArrayList<>();
				for (User user : allStudents) {
					for (Test_Score test_Score : user.getAllTestScore()) {
						if (test_Score.getCategory().equalsIgnoreCase(category)
								&& test_Score.getLevel().equalsIgnoreCase(level)) {
							response.add(dtoTestScore.convertToResponseWithUserId(test_Score, user.getUserId()));
						}
					}

				}

				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			} else if (category.equalsIgnoreCase("All Categories") && !level.equalsIgnoreCase("All levels")) {
				List<TestScoreResponseDTO> response = new ArrayList<>();
				for (User user : allStudents) {
					for (Test_Score test_Score : user.getAllTestScore()) {
						if (test_Score.getLevel().equalsIgnoreCase(level)) {
							response.add(dtoTestScore.convertToResponseWithUserId(test_Score, user.getUserId()));
						}
					}

				}

				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			} else if (!category.equalsIgnoreCase("All Categories") && level.equalsIgnoreCase("All levels")) {
				List<TestScoreResponseDTO> response = new ArrayList<>();
				for (User user : allStudents) {
					for (Test_Score test_Score : user.getAllTestScore()) {
						if (test_Score.getCategory().equalsIgnoreCase(category)) {
							response.add(dtoTestScore.convertToResponseWithUserId(test_Score, user.getUserId()));
						}
					}

				}

				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			} else {
				List<TestScoreResponseDTO> response = new ArrayList<>();
				for (User user : allStudents) {
					for (Test_Score test_Score : user.getAllTestScore()) {
						response.add(dtoTestScore.convertToResponseWithUserId(test_Score, user.getUserId()));

					}

				}

				return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);

			}

		}
	}

	// Edit Test Score By ID
	@PutMapping("/testscore/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editTestScore(@PathVariable(required = true) int id, @RequestBody Test_Score ts)
			throws Exception {
		loggerTestScore.info("Inside Edit Test Score API Call");

		Test_Score tsExists = testScoreService.getTestScoreByID(id);
		if (tsExists == null) {
			throw new ResourceNotFoundException("Test Score with ID " + id + " not found", "Id:", id);
		} else {
			testScoreService.editTestScore(ts, id);
		}
		return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

	}

	// Delete Test Score By ID
	@DeleteMapping("/testscore/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteTestScore(@PathVariable(required = true) int id) throws Exception {
		loggerTestScore.info("Inside Delete Test Score API Call");

		Test_Score tsExists = testScoreService.getTestScoreByID(id);
		if (tsExists == null) {
			throw new ResourceNotFoundException("Test Score with ID " + id + " not found", "Id", id);
		} else {
			boolean status = testScoreService.deleteTestScore(id);
			if (status)
				return new ResponseEntity<>("Test Score deleted successfully", HttpStatus.OK);
			else
				return new ResponseEntity<>("Test Score deletion failed", HttpStatus.BAD_REQUEST);
		}

	}

	// Get all student IDs
	@GetMapping("/student/getallstudentid")
	public ResponseEntity<List<Integer>> getAllStudentID() {
		List<Integer> response = new ArrayList<>();
		List<User> allStudents = userService.getAllStudents();
		for (User user : allStudents) {
			response.add(user.getUserId());
		}
		return new ResponseEntity<List<Integer>>(response, HttpStatus.OK);
	}

	// Filters students by their average test score and returns a list from highest
	// score to lowest score
	@GetMapping("/student/filter")
	public ResponseEntity<List<StudentAverageTestScoreResponseDTO>> filterStudentsByAverageTestScore(
			@RequestParam(required = false) String category, @RequestParam(required = false) String level) {
		loggerUser.info("Inside Filter Student GET API Call");

		int count = 0;
		int totalMarks = 0;
		double average = 0;
		List<User> allStudents = userService.getAllStudents();
		List<StudentAverageTestScoreResponseDTO> responseDTO = new ArrayList<>();
		if (category == null && level == null) {
			for (User user : allStudents) {
				if (!user.getAllTestScore().isEmpty()) {
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
			}
		} else if (category != null && level == null) {
			for (User user : allStudents) {
				if (!user.getAllTestScore().isEmpty()) {
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getCategory().equals(category)) {
							totalMarks += ts.getMarks();
							count++;
						}
					}
					if (count == 0 || totalMarks == 0) {
						average = 0;
					} else {
						average = totalMarks / count;
					}
					responseDTO.add(dtoUser.convertToAverageTestScoreResponse(user, (int) average));
					count = 0;
					totalMarks = 0;
					average = 0;
				}
			}
		} else if (category == null && level != null) {
			for (User user : allStudents) {
				if (!user.getAllTestScore().isEmpty()) {
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
			}
		} else if (category != null && level != null) {
			for (User user : allStudents) {
				if (!user.getAllTestScore().isEmpty()) {
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
		}
		Collections.sort(responseDTO, new Comparator<StudentAverageTestScoreResponseDTO>() {
			public int compare(StudentAverageTestScoreResponseDTO a, StudentAverageTestScoreResponseDTO b) {
				return b.getAverageScore() - a.getAverageScore();
			}
		});
		return new ResponseEntity<List<StudentAverageTestScoreResponseDTO>>(responseDTO, HttpStatus.OK);

	}

	// Sort students by their test category and returns a list
	@GetMapping("/student/sort")
	public ResponseEntity<List<UserTestScoreResponseDTO>> sortStudentsByCategory(
			@RequestParam(required = true) String category, @RequestParam(required = false) String level) {
		loggerUser.info("Inside Filter Student GET API Call");

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

	}

	// Create Question
	@PostMapping("/question/create")
	@ResponseBody
	public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody Question q) throws Exception {
		loggerQuestion.info("Inside Question Creation");

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
	}

	@GetMapping("/question/read")
	@ResponseBody
	public ResponseEntity<List<QuestionResponseDTO>> readAllQuestions() {
		loggerQuestion.info("Inside Get All Question API Call");
		List<Question> questions = questionRepository.getAllQuestions();
		List<QuestionResponseDTO> responseQuestions = new ArrayList<>();
		for (Question question : questions) {
			responseQuestions.add(dtoQuestion.convertToResponse(question));
		}

		return new ResponseEntity<List<QuestionResponseDTO>>(responseQuestions, HttpStatus.OK);
	}

	// Get Question By ID
	@GetMapping("/question/{id}")
	@ResponseBody
	public ResponseEntity<QuestionResponseDTO> readQuestion(@PathVariable(required = true) int id) throws Exception {
		loggerQuestion.info("Inside Get Question API Call");

		Question question = questionService.readQuestion(id);
		if (question == null) {
			throw new ResourceNotFoundException("Question ID not found", "Question Id", id);
		} else {
			QuestionResponseDTO questionResponse = dtoQuestion.convertToResponse(question);
			return new ResponseEntity<QuestionResponseDTO>(questionResponse, HttpStatus.OK);
		}

	}

	// Get All Questions By Category
	@GetMapping("/question/category")
	@ResponseBody
	public ResponseEntity<List<QuestionResponseDTO>> getAllQuestionByCategory(@RequestParam String category,
			@RequestParam String level) throws Exception {
		loggerQuestion.info("Inside Get Category API Call");
		int marks = 0;
		if (level.equalsIgnoreCase("Basic")) {

			marks = 1;
		} else if (level.equalsIgnoreCase("Intermediate"))
			marks = 2;
		else
			marks = 3;

		List<Question> listOfQuestions = questionRepository.getAllQuestions();
		if (listOfQuestions.isEmpty())
			throw new ResourceNotFoundException("Questions not found ", "Category", 0);
		else if (!category.equalsIgnoreCase("All Category") && !level.equalsIgnoreCase("All levels")) {
			List<QuestionResponseDTO> responseDTO = new ArrayList<>();
			listOfQuestions = questionRepository.getAllQuestionsByCategoryAndLevel(category, marks);
			for (Question question : listOfQuestions) {
				responseDTO.add(dtoQuestion.convertToResponse(question));
			}
			return new ResponseEntity<List<QuestionResponseDTO>>(responseDTO, HttpStatus.OK);
		} else if (!category.equalsIgnoreCase("All Category") && level.equalsIgnoreCase("All levels")) {
			List<QuestionResponseDTO> responseDTO = new ArrayList<>();
			listOfQuestions = questionService.getAllQuestionByCategory(category);
			for (Question question : listOfQuestions) {
				responseDTO.add(dtoQuestion.convertToResponse(question));
			}
			return new ResponseEntity<List<QuestionResponseDTO>>(responseDTO, HttpStatus.OK);
		} else if (category.equalsIgnoreCase("All Category") && !level.equalsIgnoreCase("All levels")) {
			listOfQuestions = questionRepository.getAllQuestionsByLevel(marks);
			List<QuestionResponseDTO> responseDTO = new ArrayList<>();
			for (Question question : listOfQuestions) {
				responseDTO.add(dtoQuestion.convertToResponse(question));
			}
			return new ResponseEntity<List<QuestionResponseDTO>>(responseDTO, HttpStatus.OK);
		} else {
			List<QuestionResponseDTO> responseDTO = new ArrayList<>();
			for (Question question : listOfQuestions) {
				responseDTO.add(dtoQuestion.convertToResponse(question));
			}
			return new ResponseEntity<List<QuestionResponseDTO>>(responseDTO, HttpStatus.OK);
		}
	}

	// Update Question By ID
	@PutMapping("/question/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editQuestion(@PathVariable(required = true) int id, @RequestBody Question q)
			throws Exception {
		loggerQuestion.info("Inside Edit Question API Call");
		Question questionExists = questionService.readQuestion(id);
		if (questionExists == null)
			throw new ResourceNotFoundException("Question " + id + " not found", "Id", id);
		else {
			questionService.editQuestion(questionExists, q);
		}

		return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

	}

	// Deletes a Question By ID
	@DeleteMapping("/question/delete/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteQuestion(@PathVariable(required = true) int id) throws Exception {
		loggerQuestion.info("Inside Delete Question API Call");

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

	}

}
