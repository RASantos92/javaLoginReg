package com.codingdojo.loginreg.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.codingdojo.loginreg.models.LoginUser;
import com.codingdojo.loginreg.models.User;
import com.codingdojo.loginreg.services.UserService;

@Controller
public class LoginController {

	private static UserService userServ;

	public LoginController(UserService userServ) {
		this.userServ = userServ;
	}

	@GetMapping("/")
	public String loginRegPage(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("loginUser", new LoginUser());
		return "loginPage.jsp";
	}

	@PostMapping("/register/user")
	public String processRegForm(@Valid @ModelAttribute("user") User user, BindingResult results, Model model,
			HttpSession session) {
		if (!user.getPassword().equals(user.getConfirm())) {
			results.rejectValue("password", "Match", "Confirm Password must match Password!");
		}
		if (userServ.getByEmail(user.getEmail()) != null) {
			results.rejectValue("email", "Unique", "Email already in use!");
		}
		if (results.hasErrors()) {
			model.addAttribute("loginUser", new LoginUser());
			return "loginPage.jsp";
		}
		User newUser = userServ.create(user);
		session.setAttribute("user", newUser);
		return "redirect:/success";
	}

	@PostMapping("/login/user")
	public String processLoginFrom(@Valid @ModelAttribute("loginUser") LoginUser user, BindingResult results,
			Model model, HttpSession session) {
		User loggingUser = userServ.login(user, results);
		if (results.hasErrors()) {
			model.addAttribute("user", new User());
			return "loginPage.jsp";
		}
		session.setAttribute("user", loggingUser);
		return "redirect:/success";
	}

	@GetMapping("/success")
	public String success() {
		return "success.jsp";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
