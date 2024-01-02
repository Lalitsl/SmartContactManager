package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userrepository;

//	home controller
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact Manager");
		return "home";
	}

//	about controller
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-Smart Contact Manager");
		return "about";

	}

//	sign-up controller
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
		
	}

//	ragister-user controller
	@PostMapping("do_ragister")
	public String userRagister(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("checkbox not checked !!! ");
				throw new Exception("checkbox not checked !!! ");
			}
			if (result1.hasErrors()) {
				System.out.println("Error " + result1);
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("agreement :" + agreement);
			System.out.println("USER : " + user);

			User result = this.userrepository.save(user);
			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Ragistration successfully completed ", "alert-success"));
			return "signup";
			

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("something went wrong " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	
//	custom login Controller
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login-Smart Contact Manager");
		return "login";
		
	}
	

}

