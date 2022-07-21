package com.ncs.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncs.exception.ResourceNotFoundException;
import com.ncs.model.Test_Score;
import com.ncs.repository.TestScoreRepository;

@Service
public class TestScoreServiceImpl implements TestScoreService {

	@Autowired
	TestScoreRepository testScoreRepository;

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
	public Test_Score readTestScore(int testScoreId) {
		return testScoreRepository.readTestScore(testScoreId);
	}

	@Override
	public List<Test_Score> readAllTestScoreByUserID(int userId) {
		return testScoreRepository.readAllTestScoreByUserID(userId);
	}

	@Override
	public void editTestScore(Test_Score ts) {
		int id = ts.getTestId();

		Test_Score editedTestScore = testScoreRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Test Score with ID: " + id + " not found", "Id", id));
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

}
