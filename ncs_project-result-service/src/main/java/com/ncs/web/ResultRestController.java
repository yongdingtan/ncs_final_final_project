package com.ncs.web;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ncs.dto.ResultResponseDTO;
import com.ncs.dto.TestScoreResponseDTO;
import com.ncs.model.Question;
import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.service.QuestionService;
import com.ncs.service.TestScoreService;
import com.ncs.service.UserService;
import com.ncs.util.JWTUtil;
import com.ncs.util.TestScoreDTOConversion;
import com.ncs.util.UserDTOConversion;

@Validated
@RestController
@CrossOrigin(origins = { "http://localhost:8093", "http://localhost:4200" }, allowedHeaders = "*")
@RequestMapping("/result")
public class ResultRestController {

	UserService userService;
	TestScoreService testScoreService;
	QuestionService questionService;

	@Autowired
	TestScoreDTOConversion testScoreConversion;

	@Autowired
	UserDTOConversion userConversion;

	@Autowired
	JWTUtil jwtUtil;

	@Autowired
	RestTemplate restTemplate;

	private static final Logger loggerUser = LoggerFactory.getLogger(User.class);
	private static final Logger loggerQuestion = LoggerFactory.getLogger(Question.class);

	@Autowired
	public ResultRestController(UserService userService, TestScoreService testScoreService,
			QuestionService questionService) {
		System.out.println("Inside Student Rest Controller Constructor");
		this.userService = userService;
		this.testScoreService = testScoreService;
		this.questionService = questionService;
	}

	// Get all results unique to current user (Student)
	@GetMapping("/results")
	public ResponseEntity<List<ResultResponseDTO>> getStudentResults(@RequestParam(required = true) Date date,
			@RequestParam(required = false) String category, @RequestParam(required = false) String level,
			@RequestParam(required = false) Long id) throws Exception {
		loggerUser.info("Inside Get Results API Call");
		int studentId = 0;
		if (id != null) {
			studentId = id.intValue();
		}

		List<User> allStudents = userService.getAllUsersByRole("student");
		List<ResultResponseDTO> allResults = new ArrayList<>();
		if (category == null && level == null && studentId == 0) {
			for (User user : allStudents) {
				List<TestScoreResponseDTO> testScores = new ArrayList<>();
				for (Test_Score ts : user.getAllTestScore()) {
					if (ts.getDate().equals(date)) {
						testScores.add(testScoreConversion.convertToResponse(ts));
					}
				}
				allResults.add(userConversion.convertToResultResponse(user, testScores));
			}
		} else if (category != null && level == null && studentId == 0) {
			for (User user : allStudents) {
				List<TestScoreResponseDTO> testScores = new ArrayList<>();
				for (Test_Score ts : user.getAllTestScore()) {
					if (ts.getDate().equals(date) && ts.getCategory().equals(category)) {
						testScores.add(testScoreConversion.convertToResponse(ts));
					}
				}
				allResults.add(userConversion.convertToResultResponse(user, testScores));
			}

		} else if (category == null && level != null && studentId == 0) {
			for (User user : allStudents) {
				List<TestScoreResponseDTO> testScores = new ArrayList<>();
				for (Test_Score ts : user.getAllTestScore()) {
					if (ts.getDate().equals(date) && ts.getLevel().equals(level)) {
						testScores.add(testScoreConversion.convertToResponse(ts));
					}
				}
				allResults.add(userConversion.convertToResultResponse(user, testScores));
			}

		} else if (category == null && level == null && studentId > 0) {
			for (User user : allStudents) {
				if (user.getUserId() == studentId) {
					List<TestScoreResponseDTO> testScores = new ArrayList<>();
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getDate().equals(date)) {
							testScores.add(testScoreConversion.convertToResponse(ts));
						}
					}
					allResults.add(userConversion.convertToResultResponse(user, testScores));
				}

			}

		} else if (category != null && level != null && studentId == 0) {
			for (User user : allStudents) {
				List<TestScoreResponseDTO> testScores = new ArrayList<>();
				for (Test_Score ts : user.getAllTestScore()) {
					if (ts.getDate().equals(date) && ts.getCategory().equals(category) && ts.getLevel().equals(level)) {
						testScores.add(testScoreConversion.convertToResponse(ts));
					}
				}
				allResults.add(userConversion.convertToResultResponse(user, testScores));
			}

		} else if (category == null && level != null && studentId > 0) {
			for (User user : allStudents) {
				if (user.getUserId() == studentId) {

					List<TestScoreResponseDTO> testScores = new ArrayList<>();
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getDate().equals(date) && ts.getLevel().equals(level)) {
							testScores.add(testScoreConversion.convertToResponse(ts));
						}
					}
					allResults.add(userConversion.convertToResultResponse(user, testScores));
				}
			}

		} else if (category != null && level != null && studentId > 0) {
			for (User user : allStudents) {
				if (user.getUserId() == studentId) {

					List<TestScoreResponseDTO> testScores = new ArrayList<>();
					for (Test_Score ts : user.getAllTestScore()) {
						if (ts.getDate().equals(date) && ts.getCategory().equals(category)
								&& ts.getLevel().equals(level)) {
							testScores.add(testScoreConversion.convertToResponse(ts));
						}
					}
					allResults.add(userConversion.convertToResultResponse(user, testScores));
				}
			}

		}

		return new ResponseEntity<List<ResultResponseDTO>>(allResults, HttpStatus.OK);

	}

}
