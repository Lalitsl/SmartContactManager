package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.model.MailStructure;
import com.smart.service.MailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random(1000);

	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//	forgot password form open handler 
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgotEmailForm";
	}

//	OTP send handler

	@PostMapping("/otpSend")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("Email : " + email);

//		generate OTP of 4 digit 
		int otp = random.nextInt(9999);
		System.out.println("otp : " + otp);

		// Send OTP to the provided email
		try {
			MailStructure mailStructure = new MailStructure();
			mailStructure.setSubject("Verify OTP for Password Reset of smart-contact-manager project ");
			mailStructure.setMessage(
					" Please check your email and verify OTP to continue forgot password process . Your OTP is: " + otp
							+ " Thank-you Team Smart-contact-manager.");
			this.mailService.sendMail(email, mailStructure);
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verifyOTP";
		} catch (Exception e) {
			
			System.out.println("something wrong ");
			return "forgotEmailForm";
		}
		
	}


	@PostMapping("/verifyOtp")
	public String verifyOtp(@RequestParam("otp") int otp , HttpSession session) {
		int myOtp= (int) session.getAttribute("myotp");
		String Email=(String) session.getAttribute("email");
		System.out.println("MYOTP : " + myOtp);
		System.out.println("User OTP : " + otp);
		if(myOtp==otp) {
//			change password code
			User user = userRepository.getUserByUserName(Email);
			if(user==null) {
//				error message 
				session.setAttribute("mess","Email does't exist . Email not matched in database ");
				return "forgotEmailForm";
			}else {
//				send change password form 
				
			}
			System.out.println("OTP MATCHED ........");
			return "changePasswordForm";
			
		}else {
			session.setAttribute("message", new Message("Invalid OTP. Please Try Again...", "danger"));
			System.out.println("OTP INVALID ........");
			return "verifyOTP";
		}
		
	}
	
//	password change controller 
	
	@PostMapping("/changePassword")
	public String changePass(@RequestParam("newPassword") String newPassword , HttpSession session) {
		
		String email= (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changged successfully .....";
	}
	
	
	
	
	
	
	
}



