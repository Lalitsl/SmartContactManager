package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import com.razorpay.*;
import com.razorpay.RazorpayClient;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

//	method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
//		get the user using username(email)
		String username = principal.getName();
		User user = userRepository.getUserByUserName(username);
		System.out.println("usernnnaem " + user);
		model.addAttribute("user", user);

	}

//	home dashboard handler
	@RequestMapping("/dashboard")
	public String userDashboard(Model model, Principal principal) {
		model.addAttribute("title", "user dashboard");
		return "normal/userDashboard";
	}

//	open  add contact form handler
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "user-Add-contact-form");
		model.addAttribute("contact", new Contact());
		return "normal/addContact";
	}

//	process contact form (save contact form ) handler
	@PostMapping("/processContact")
	public String processContactForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
//			if(3>2) {
//				throw new Exception();
//			}

//			processing and uploading image 
			if (file.isEmpty()) {
				// file is empty then run this code
				System.out.println("file is empty");
				contact.setImage("Contacts_logo.png");

			} else {
				// file is select then update the name to contact
				contact.setImage(file.getOriginalFilename());
				// getting the path
				File saveFile = new ClassPathResource("static/img").getFile();
				// getting path
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				// 1.input path, 2.copy path, 3. copy option
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded successfully ");

			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("data added to database");

			session.setAttribute("message", new Message("Contact added successfully !!! ", "success"));
//			HttpSession session1 = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
//			session1.removeAttribute("message");

			System.out.println("DATA : " + contact);
		} catch (Exception e) {
			System.out.println("add contact form error " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong. Please try Again !!! ", "danger"));

		}
		return "normal/addContact";

	}

//		show contacts handler 

	// per page= 5 contact
	// current page = page
	@GetMapping("/showAllContacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "show all contacts ");

		// get all contacts
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		// pagination
		Pageable pageable = PageRequest.of(page, 2);
		/*
		 * pageable ke pass 2 object hoge ==> 1. current page = page 2. contact per page
		 * =5
		 */
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", contacts.getTotalPages());
		return "normal/showAllContacts";

	}

//	show particular contact details handler

	@RequestMapping("/contact/{cId}")
	public String showParticularContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		System.out.println("CID... " + cId);
		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		Contact contact = optionalContact.get();

//		security check
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("con", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/showParticularDetails";

	}

//	delete particular  contact 
	@GetMapping("/deleteContact/{cid}")
	public String deleteParticularContact(@PathVariable("cid") Integer cId, Model model, Principal principal,
			HttpSession session) {
		Optional<Contact> optionalContact = this.contactRepository.findById(cId);
		Contact contact = optionalContact.get();

//		security check
		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
//			contact.setUser(null);
//			this.contactRepository.delete(contact);

			User user1 = this.userRepository.getUserByUserName(username);
			user1.getContacts().remove(contact);
			this.userRepository.save(user1);

			session.setAttribute("message", new Message("contact deleted successfully ", "success"));
			System.out.println("user deleteed ");

		}
		return "redirect:/user/showAllContacts/0";
	}

//	update contact handler
	@PostMapping("/updatecontact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model model) {

		model.addAttribute("title", "update contact");
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		return "normal/updateContactForm";

	}

//	update contact form processor

	@PostMapping("/updateProcessContact")
	public String updateProcessContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {

//		get old contact( assume user)
		Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
		try {
			if (!file.isEmpty()) {

				// image delete
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();

				// image update

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldContactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("contact update successfully ", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("username : " + contact.getName());
		System.out.println("id : " + contact.getcId());
//		return "redirect:/user/contact/"+contact.getcId();
		return "redirect:/user/showAllContacts/0";

	}

//	your profile handler
	@GetMapping("/yourProfile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "your profile page");
		return "normal/yourProfile";

	}

//	open setting handler
	@GetMapping("settings")
	public String OpenSettings() {
		return "normal/settings";
	}

//	change password handler

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		String username = principal.getName();
		User currentuser = this.userRepository.getUserByUserName(username);
		if (this.bCryptPasswordEncoder.matches(oldPassword, currentuser.getPassword())) {
			currentuser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentuser);
			session.setAttribute("message", new Message("Password Changed successfully", "success"));
			return "redirect:/logout";
		} else {
			// error ....
			session.setAttribute("message", new Message("Your Old Password Invalid !!! ", "danger"));
			return "redirect:/user/settings";

		}

	}

//	create order for payment gatway 

	@PostMapping("/createOrder")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data ) throws Exception {
		System.out.println("This is order function ");
		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());
		var client=new RazorpayClient("rzp_test_SjOHuxyf14nY18", "WZzyPendOl1c1UAdfsX1umha");
		
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_12345");
		
//		 creating new order
		Order order = client.orders.create(ob);
		System.out.println(order);
		return order.toString();
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}


