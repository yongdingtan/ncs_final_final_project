package com.ncs.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.repository.TestScoreRepository;
import com.ncs.repository.UserRepository;

@Service
public class TestScoreServiceImpl implements TestScoreService {

	@Autowired
	TestScoreRepository testScoreRepository;
	@Autowired
	UserRepository userRepository;

	@Override
	public Test_Score createTestScore(Test_Score ts) {
		if (ts != null) {
			Date date = Date.valueOf(LocalDate.now());
			ts.setDate_created(date);
			Test_Score savedEntity = testScoreRepository.save(ts);
			return savedEntity;

		} else {
			throw new NullPointerException("Question Info Is Null");
		}
	}

	@Override
	public Set<Test_Score> readTestScore(int studentId) {
		User user = userRepository.findUserById(studentId);
		return user.getAllTestScore();
	}

	@Override
	public List<Test_Score> readAllTestScoreByUserID(int userId) {
		return testScoreRepository.readAllTestScoreByUserID(userId);
	}

	@Override
	public void editTestScore(Test_Score ts, int studentId) {
		User user = userRepository.findUserById(studentId);
		Test_Score editedTestScore = new Test_Score();
		Set<Test_Score> userTest_Score = user.getAllTestScore();
		for (Test_Score test_Score : userTest_Score) {
			if (test_Score.getTestId() == ts.getTestId())
				editedTestScore = test_Score;
		}
		if (editedTestScore.getDate() != null)
			editedTestScore.setDate(ts.getDate());
		if (editedTestScore.getLevel() != null)
			editedTestScore.setLevel(ts.getLevel());
		if (editedTestScore.getMarks() != 0)
			editedTestScore.setMarks(ts.getMarks());
		if (editedTestScore.getTotalScore() != 0)
			editedTestScore.setTotalScore(ts.getTotalScore());
		editedTestScore.setDate_updated(new Timestamp(System.currentTimeMillis()));
		testScoreRepository.save(editedTestScore);
	}

	@Override
	public boolean deleteTestScore(int testScoreId) {
		return testScoreRepository.deleteTestScore(testScoreId);
	}

	@Override
	public Test_Score getTestScoreByID(int testScoreId) {
		// TODO Auto-generated method stub
		return testScoreRepository.getTestScoreByID(testScoreId);
	}

}
