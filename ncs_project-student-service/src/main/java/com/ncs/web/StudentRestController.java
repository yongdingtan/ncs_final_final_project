package com.ncs.web;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.ncs.exception.ResourceNotFoundException;
import com.ncs.model.Question;
import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.service.QuestionService;
import com.ncs.service.TestScoreService;
import com.ncs.service.UserService;
import com.ncs.util.JWTUtil;
import com.ncs.util.QuestionDTOConversion;
import com.ncs.util.TestScoreDTOConversion;

import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping("/student")
public class StudentRestController {

	UserService userService;
	TestScoreService testScoreService;
	QuestionService questionService;

	@Autowired
	JWTUtil jwtUtil;

	@Autowired
	RestTemplate restTemplate;

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

	// Update Student
	@PutMapping("/edit/{id}")
	@ResponseBody
	public ResponseEntity<?> editUser(@RequestHeader(name = "Authorization") String token, @PathVariable int id,
			@RequestBody User u) throws Exception {
		loggerUser.info("Inside Edit User API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "student");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			User userExists = userService.findUserById(id);
			if (userExists == null) {
				throw new ResourceNotFoundException("Student with ID: " + id + " not found", "Id", id);
			} else {
				userService.editUser(u);
			}

			return new ResponseEntity<>("Details were updated successfully", HttpStatus.OK);

		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Get Exam Questions
	@GetMapping("/exam/attempt")
	@ResponseBody
	public List<QuestionResponseDTO> getExamQuestions(@RequestHeader(name = "Authorization") String token,
			@RequestParam String category, @RequestParam String level, HttpSession session) throws Exception {
		loggerQuestion.info("Inside Get Exam Questions API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "student");

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			HashMap<String, List<QuestionResponseDTO>> examQuestions = new HashMap<>();
			List<Question> rawQuestions = questionService.getExamQuestions(category, level);
			if (rawQuestions == null)
				throw new ResourceNotFoundException("Questions not found ", "Category", 0);
			else {
				List<QuestionResponseDTO> allQuestions = new ArrayList<>();
				int count = 1;
				for (Question question : rawQuestions) {
					question.setQuestionNumber(count++);
					allQuestions.add(QuestionDTOConversion.convertToResponse(question));
				}
				examQuestions.put(category, allQuestions);
				session.setAttribute("examQuestions", examQuestions);

				return allQuestions;
			}
		} else {
			throw new Exception("Invalid Credentials");
		}

	}

	// Answer a list of questions
	@PostMapping("/exam/answer")
	@ResponseBody
	public ResponseEntity<TestScoreResponseDTO> answerQuestions(@RequestHeader(name = "Authorization") String token,
			@RequestBody ArrayList<String> answers, @RequestParam String category, @RequestParam String level)
			throws Exception {
		loggerQuestion.info("Inside Answer Exam Questions API Call");

		String endPoint = "http://NCS-PROJECT-PUBLIC-SERVICE/public/validate";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", token);
		headers.set("userType", "student");

		String user = null;
		if (token != null) {
			// parse the token.

			try {
				user = Jwts.parser().setSigningKey("secret").parseClaimsJws(token.replace("ncs-", "")).getBody()
						.getSubject();

			} catch (Exception e) {

				throw e;

			}
		}

		HttpEntity<String> header = new HttpEntity<String>(headers);
		ResponseEntity<Boolean> result = restTemplate.exchange(endPoint, HttpMethod.GET, header, Boolean.class);
		boolean jwtStatus = result.getBody();

		if (jwtStatus) {
			ResponseEntity<List<QuestionResponseDTO>> examQuestions = restTemplate
					.exchange(
							"http://NCS-PROJECT-QUESTION-SERVICE/question/exam/attempt" + "?category=" + category
									+ "&level=" + level,
							HttpMethod.GET, null, new ParameterizedTypeReference<List<QuestionResponseDTO>>() {
							});
			List<QuestionResponseDTO> questions = examQuestions.getBody();
			ArrayList<String> correctAnswers = new ArrayList<>();

			int totalMarks = 0;

			QuestionResponseDTO DTOQuestion = questions.get(0);

			for (QuestionResponseDTO dto : questions) {
				correctAnswers.add(dto.getCorrectAnswer());
			}

			for (int i = 0; i < 20; i++) {
				if (correctAnswers.get(i).equals(answers.get(i)))
					totalMarks++;
			}

			Date date = Date.valueOf(LocalDate.now());
			String questionLevel = "";
			if (DTOQuestion.getQuestionMarks() == 1)
				questionLevel = "Basic";
			else if (DTOQuestion.getQuestionMarks() == 2)
				questionLevel = "Intermediate";
			else if (DTOQuestion.getQuestionMarks() == 3)
				questionLevel = "Advanced";
			int totalScore = totalMarks * 5;

			Test_Score testScore = new Test_Score();
			testScore.setLevel(level);
			testScore.setCategory(DTOQuestion.getQuestionCategory());
			testScore.setDate(date);
			testScore.setMarks(totalScore);
			testScore.setTotalScore(20);

			User u = userService.findUserByUsername(user);
			userService.saveScore(u, testScore);

			TestScoreResponseDTO userTestScore = TestScoreDTOConversion
					.convertToResponse(testScoreService.createTestScore(testScore));

			return new ResponseEntity<TestScoreResponseDTO>(userTestScore, HttpStatus.OK);
		} else {
			throw new Exception("Invalid Credentials");
		}

	}
}
