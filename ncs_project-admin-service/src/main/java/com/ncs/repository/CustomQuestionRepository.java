package com.ncs.repository;

import java.util.List;

import com.ncs.model.Question;

public interface CustomQuestionRepository {

	public Question readQuestion(int questionId);

	public List<Question> getAllQuestionByCategory(String category);

	public List<Question> getExamQuestions(String category, String level);

	public boolean deleteQuestion(int questionId);

	public List<Question> getAllQuestionsByCategoryAndLevel(String category, int level);

	public List<Question> getAllQuestionsByLevel(int level);

	public List<String> getAllQuestionCategory();

}
