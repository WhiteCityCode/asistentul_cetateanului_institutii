package com.govac.institutii;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.govac.institutii.db.User;
import com.govac.institutii.db.UserRepository;

@RestController
@RequestMapping("/api")
public class UserRestController {
	
	private final UserRepository userRepo;

	@RequestMapping(value = "/users", method = RequestMethod.GET)
    Collection<User> users() {
        return userRepo.findAll();
	}
	
	@Autowired
    UserRestController(UserRepository usrRepo) {
	        this.userRepo = usrRepo;
	}
}
