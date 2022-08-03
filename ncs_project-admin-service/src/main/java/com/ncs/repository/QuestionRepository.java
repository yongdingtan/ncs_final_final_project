package com.ncs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ncs.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer>, CustomQuestionRepository {
	@Query("from Question q where q.is_available = 1")
	public List<Question> getAllQuestions();

}
