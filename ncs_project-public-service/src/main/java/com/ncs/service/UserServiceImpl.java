package com.ncs.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ncs.exception.ResourceNotFoundException;
import com.ncs.model.MyUserDetails;
import com.ncs.model.Test_Score;
import com.ncs.model.User;
import com.ncs.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Override
	public User saveUser(User u) {
		if (u != null) {
			Date date = Date.valueOf(LocalDate.now());
			u.setDate_created(date);
			User savedEntity = userRepository.save(u);
			return savedEntity;

		} else {
			throw new NullPointerException("User Info Is Null");
		}
	}

	@Override // from UserDetailsService
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return MyUserDetails.build(user);
	}

	@Override
	public User findUserById(int userId) {
		return userRepository.findUserById(userId);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserPassword(String password) {
		return userRepository.findByPassword(password);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public List<User> getAllUsersByRole(String role) {
		return userRepository.getAllUsersByRole(role);
	}

	@Override
	public void editUser(User u) {
		int id = u.getUserId();

		User editedUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found", "Id", id));

		if (editedUser.getUsername() != null) {
			editedUser.setUsername(u.getUsername());
		}
		if (editedUser.getPassword() != null) {
			editedUser.setPassword(u.getPassword());
		}

		editedUser.setDate_updated(new Timestamp(System.currentTimeMillis()));
		userRepository.save(editedUser);
	}

	@Override
	public boolean deleteUser(int userId) {
		return userRepository.deleteUser(userId);
	}

	@Override
	public void saveScore(User u, Test_Score ts) {
		int id = u.getUserId();
		User editedUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found", "Id", id));
		Set<Test_Score> allTestScores = editedUser.getAllTestScore();
		if (ts != null) {
			allTestScores.add(ts);
		}
		editedUser.setAllTestScore(allTestScores);
		editedUser.setDate_created(new Date(System.currentTimeMillis()));
	}

}
