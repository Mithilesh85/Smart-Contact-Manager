package com.mithilesh.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mithilesh.smart.dao.ContactRepository;
import com.mithilesh.smart.dao.UserRepository;
import com.mithilesh.smart.entities.Contact;
import com.mithilesh.smart.entities.User;
import com.mithilesh.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	// method for adding common data for response

	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		String userName = principal.getName();
		System.out.println("USERNAME" + userName);
		User user = userRepository.getUserByUserName(userName);

		System.out.println("USER" + user);

		m.addAttribute("user", user);

	}

//   dashboard home

	@RequestMapping("/index")
	public String dashboard(Model m, Principal p) {

		m.addAttribute("title", "User Dashboard-Smart Contact Manager");

		return "normal/user_dashboard";
	}

//	open contact form handler

	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add Contact-Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process_contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// just for making exception

//			if(3>2) {
//				throw new Exception(s);
//			}

//			processing and uploading the file

			if (file.isEmpty()) {
				// just show a message
				System.out.println("image is not selected......");
				
				contact.setImage("contact.png");
				
			} else {
				// upload the file and update the name in contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("image is uploaded...");

			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);

			System.out.println("DATA" + contact);
			System.out.println("added data to data base");

//			success message
			session.setAttribute("message", new Message("your contact is saved please add more!!", "success"));

		} catch (Exception e) {

			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();

//			error message

			session.setAttribute("message", new Message("something went wrong please try again!!", "danger"));

		}

		return "normal/add_contact_form";
	}

	// show contact handler

	@GetMapping("/show_contact/{page}")
	public String showContact(@PathVariable("page") Integer page,Model m,Principal principal) {

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page,5);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		m.addAttribute("title", "Show Contact-Smart Contact Manager");
		return "normal/show_contact";
	}
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model m)
	{
		System.out.println("CID"+cId);
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		m.addAttribute("contact",contact);
		
		
		return "normal/contact_detail";
	}

}
