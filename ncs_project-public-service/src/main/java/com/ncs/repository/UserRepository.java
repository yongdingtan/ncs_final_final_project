package com.ncs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ncs.model.User;

public interface UserRepository extends JpaRepository<User, Integer>, CustomUserRepository {
	@Query("from User u where u.username = :username AND u.is_available = 1")
	public User getUsersByUsername(String username);

	Optional<User> findByUsername(String username);
}
