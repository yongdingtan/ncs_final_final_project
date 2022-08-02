package com.ncs.dto;

import java.sql.Date;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ncs.model.User;

@Component
public class TestScoreResponseDTO {

	private int testId;
	private int userId;
	private Date date;
	private String category;
	private String level;
	private int totalScore;
	private int marks;

	public TestScoreResponseDTO() {
		super();
	}

	public TestScoreResponseDTO(int testId, int userId, User user, Date date, String category, String level,
			int totalScore, int marks) {
		super();
		this.testId = testId;
		this.userId = userId;
		this.date = date;
		this.category = category;
		this.level = level;
		this.totalScore = totalScore;
		this.marks = marks;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getMarks() {
		return marks;
	}

	public void setMarks(int marks) {
		this.marks = marks;
	}

	@Override
	public String toString() {
		return "TestScoreResponseDTO [testId=" + testId + ", userId=" + userId + ", date=" + date + ", category="
				+ category + ", level=" + level + ", totalScore=" + totalScore + ", marks=" + marks + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, date, level, marks, testId, totalScore, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestScoreResponseDTO other = (TestScoreResponseDTO) obj;
		return Objects.equals(category, other.category) && Objects.equals(date, other.date)
				&& Objects.equals(level, other.level) && marks == other.marks && testId == other.testId
				&& totalScore == other.totalScore && userId == other.userId;
	}

}
