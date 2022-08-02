package com.ncs.web;

import java.security.SignatureException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ncs.dto.StudentTestScoreResponseDTO;
import com.ncs.dto.TestScoreResponseDTO;
import com.ncs.exception.ResourceNotFoundException;
import com.ncs.model.Question;
import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.repository.TestScoreRepository;
import com.ncs.service.QuestionService;
import com.ncs.service.TestScoreService;
import com.ncs.service.UserService;
import com.ncs.util.AuthTokenFilter;
import com.ncs.util.JWTUtil;
import com.ncs.util.TestScoreDTOConversion;

@Validated
@RestController
@CrossOrigin(origins = { "http://localhost:8091", "http://localhost:4200" }, allowedHeaders = "*")
@RequestMapping("/student")
public class StudentRestController {

	UserService userService;
	TestScoreService testScoreService;
	QuestionService questionService;

	@Autowired
	TestScoreDTOConversion dtoConversion;

	@Autowired
	JWTUtil jwtUtil;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	AuthTokenFilter tokenFilter;

	@Autowired
	TestScoreRepository testScoreRepository;

	private static final Logger loggerUser = LoggerFactory.getLogger(User.class);
	private static final Logger loggerQuestion = LoggerFactory.getLogger(Question.class);

	@Autowired
	public StudentRestController(UserService userService, TestScoreService testScoreService,
			QuestionService questionService) {
		System.out.println("Inside Student Rest Controller Constructor");
		this.userService = userService;
		this.testScoreService = testScoreService;
		this.questionService = questionService;
	}

	// Generic function to convert set to list
	public static <T> List<T> convertSetToList(Set<T> set) {
		// create an empty list
		List<T> list = new ArrayList<>();

		// push each element in the set into the list
		for (T t : set)
			list.add(t);

		// return the list
		return list;
	}

	@GetMapping("/results/{username}")
	public ResponseEntity<List<TestScoreResponseDTO>> getAllResults(@PathVariable(required = true) String username,
			@Context HttpServletRequest request) throws SignatureException {
		User user = userService.findUserByUsername(username);

		List<TestScoreResponseDTO> response = new ArrayList<>();
		for (Test_Score ts : user.getAllTestScore()) {
			if (ts.isIs_available() == true)
				response.add(dtoConversion.convertToResponseWithUserId(ts, user.getUserId()));
		}

		return new ResponseEntity<List<TestScoreResponseDTO>>(response, HttpStatus.OK);
	}

	// Get all results unique to current user (Student)
	@GetMapping("/results/userId")
	public ResponseEntity<List<TestScoreResponseDTO>> getStudentResults(@RequestParam(required = true) int userId)
			throws Exception {
		loggerUser.info("Inside Get User API Call");

		User userExists = userService.findUserById(userId);
		if (userExists == null) {
			throw new ResourceNotFoundException("Student with user ID: " + userId + " not found", "Id", 0);
		} else {
			List<Test_Score> allTestResults = convertSetToList(userExists.getAllTestScore());
			List<TestScoreResponseDTO> resultsToShow = new ArrayList<>();
			for (Test_Score testScore : allTestResults) {
				resultsToShow.add(dtoConversion.convertToResponse(testScore));
			}
			return new ResponseEntity<List<TestScoreResponseDTO>>(resultsToShow, HttpStatus.OK);
		}

	}

	// Answer a list of questions
	@PostMapping("/exam/answer/{username}")
	public ResponseEntity<StudentTestScoreResponseDTO> answerQuestions(@PathVariable(required = true) String username,
			@Valid @RequestBody HashMap<Integer, String> answers) throws Exception {
		loggerQuestion.info("Inside Answer Exam Questions API Call");
		int totalScore = 0;
		Question DTOQuestion = new Question();

		for (Map.Entry<Integer, String> set : answers.entrySet()) {
			Question question = questionService.readQuestion(set.getKey());
			DTOQuestion = question;
			if (question.getCorrectAnswer().equals(set.getValue())) {
				totalScore++;
			}
		}

		Date date = Date.valueOf(LocalDate.now());
		String questionLevel = "";
		if (DTOQuestion.getQuestionMarks() == 1)
			questionLevel = "Basic";
		else if (DTOQuestion.getQuestionMarks() == 2)
			questionLevel = "Intermediate";
		else if (DTOQuestion.getQuestionMarks() == 3)
			questionLevel = "Advanced";
		totalScore = totalScore * 5;

		Test_Score testScore = new Test_Score();
		testScore.setLevel(questionLevel);
		testScore.setCategory(DTOQuestion.getQuestionCategory());
		testScore.setDate(date);
		testScore.setMarks(totalScore);
		testScore.setTotalScore(100);

		User u = userService.findUserByUsername(username);
		userService.saveScore(u, testScore);

		int numberOfStudentsAboveYou = 0;
		numberOfStudentsAboveYou = testScoreService.getStudentsAboveYou(testScore, u);
		int numberOfStudentsBeneathYou = 0;
		numberOfStudentsBeneathYou = testScoreService.getStudentsBeneathYou(testScore, u);

		StudentTestScoreResponseDTO userTestScore = dtoConversion.convertToStudentResponseDTO(
				testScoreService.createTestScore(testScore), numberOfStudentsAboveYou, numberOfStudentsBeneathYou);

		return new ResponseEntity<StudentTestScoreResponseDTO>(userTestScore, HttpStatus.OK);

	}
}
