package com.ncs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ncs.model.Test_Score;

public interface TestScoreRepository extends JpaRepository<Test_Score, Integer>, CustomTestScoreRepository {

}
