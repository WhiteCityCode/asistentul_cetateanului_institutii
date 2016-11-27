package com.govac.institutii;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.govac.institutii.security.JwtAuthenticationRequest;
import com.govac.institutii.security.JwtAuthenticationResponse;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;

@RestController
@RequestMapping("/api/")
public class AuthRestController {
	
	@Value("${jwt.header}")
    private String tokenHeader;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;
    
    private final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "${jwt.route.auth}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
    	@RequestBody JwtAuthenticationRequest authenticationRequest, 
    	Device device) throws AuthenticationException {

        // For later on, if GovIT Auth not ready
		//        final Authentication authentication = authenticationManager.authenticate(
		//                new UsernamePasswordAuthenticationToken(
		//                        authenticationRequest.getEmail(),
		//                        authenticationRequest.getPassword()
		//                )
		//        );
		//        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final JwtUser userDetails = (JwtUser) userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        logger.info("Generating token for " + userDetails.getEmail() + " and device " + device.toString());
        final String token = jwtTokenUtil.generateToken(userDetails, device);

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "${jwt.route.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (!jwtTokenUtil.canTokenBeRefreshed(token))
			return ResponseEntity.badRequest().body(null);
        Optional<String> refr = jwtTokenUtil.refreshToken(token);
        return refr
        	.map((r) -> {return ResponseEntity.ok(new JwtAuthenticationResponse(r));})
        	.orElse(ResponseEntity.badRequest().body(null));
    }
}
