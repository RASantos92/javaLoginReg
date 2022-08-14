package com.codingdojo.loginreg.services;

import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.codingdojo.loginreg.models.LoginUser;
import com.codingdojo.loginreg.models.User;
import com.codingdojo.loginreg.repositorys.UserRepository;

@Service
public class UserService {
	private static UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	public User getOne(Long id) {
		Optional<User> user = userRepo.findById(id);
		return user.isPresent() ? user.get() : null;
	}

	public User getByEmail(String email) {
		Optional<User> user = userRepo.findByEmail(email);
		return user.isPresent() ? user.get() : null;
	}

	public List<User> getAll() {
		return (List<User>) userRepo.findAll();
	}

	public User create(User user) {
		user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
		return userRepo.save(user);
	}

	public User update(User user) {
		return userRepo.save(user);
	}

	public void delete(Long id) {
		userRepo.deleteById(id);
	}

	public User login(LoginUser loginUser, BindingResult result) {
		if (result.hasErrors()) {
			return null;
		}
		User existingUser = getByEmail(loginUser.getEmail());
		if (existingUser == null) {
			result.rejectValue("email", "Unique", "Unknown email!");
			return null;
		}
		if (!BCrypt.checkpw(loginUser.getPassword(), existingUser.getPassword())) {
			result.rejectValue("password", "Matches", "Incorrect Password!");
			return null;
		}
		return existingUser;
	}

}
