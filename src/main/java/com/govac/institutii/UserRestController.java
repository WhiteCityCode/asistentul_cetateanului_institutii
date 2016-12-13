package com.govac.institutii;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.govac.institutii.db.User;
import com.govac.institutii.db.UserRepository;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;
import com.govac.institutii.validation.UserAdminDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/")
public class UserRestController {

    @Autowired
    private UserRepository userRepo;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Page<User> users(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return userRepo.findAll(new PageRequest(page, size));
    }
    
    @RequestMapping(value = "users", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(
            @RequestBody @Validated UserAdminDTO user) {
        User toSaveUser = new User(
                user.cnp, user.email, user.firstName, user.lastName, user.phone
        );
        toSaveUser.setRole(user.role);
        User savedUser = userRepo.save(toSaveUser);
        return ResponseEntity.ok(savedUser);
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public ResponseEntity<?> getAuthenticatedUser(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        Optional<String> email = jwtTokenUtil.getSubjectFromToken(token);
        return email
                .map((e) -> {
                    JwtUser usr = (JwtUser) userDetailsService.loadUserByUsername(e);
                    if (null == usr) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    return ResponseEntity.ok(usr);
                })
                .orElse(ResponseEntity.badRequest().body(null));
    }
}
