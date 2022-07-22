package com.ncs.util;

import com.ncs.dto.StudentAverageTestScoreResponseDTO;
import com.ncs.dto.UserResponseDTO;
import com.ncs.model.User;

public class UserDTOConversion {

	public static UserResponseDTO convertToResponse(User u) {
		UserResponseDTO dto = new UserResponseDTO();

		dto.setUserId(u.getUserId());
		dto.setUsername(u.getUsername());
		dto.setRole(u.getRole());
		dto.setEmail(u.getEmail());
		dto.setAllTestScore(u.getAllTestScore());

		return dto;
	}

	public static UserResponseDTO convertToRoleResponse(User u) {
		UserResponseDTO dto = new UserResponseDTO();

		dto.setUserId(u.getUserId());
		dto.setUsername(u.getUsername());
		dto.setRole(u.getRole());
		dto.setEmail(u.getEmail());

		return dto;
	}

	public static StudentAverageTestScoreResponseDTO convertToAverageTestScoreResponse(User u, int averageTestScore) {

		StudentAverageTestScoreResponseDTO dto = new StudentAverageTestScoreResponseDTO();

		dto.setUserId(u.getUserId());
		dto.setUsername(u.getUsername());
		dto.setRole(u.getRole());
		dto.setEmail(u.getEmail());
		dto.setAverageScore(averageTestScore);

		return dto;

	}

}
