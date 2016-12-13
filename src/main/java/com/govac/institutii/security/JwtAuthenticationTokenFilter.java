package com.govac.institutii.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(
    		HttpServletRequest request, 
    		HttpServletResponse response, 
    		FilterChain chain
    	) throws ServletException, IOException {
    	
        String authToken = request.getHeader(this.tokenHeader);
        Optional<String> email = jwtTokenUtil.getSubjectFromToken(authToken);
        email.ifPresent((e) -> {
        	
        	logger.info("Checking token for " + e);        	
//        	if (null == SecurityContextHolder.getContext().getAuthentication())
//        		return;
        	
        	JwtUser userDetails = (JwtUser) this.userDetailsService.loadUserByUsername(e);
            if (!jwtTokenUtil.validateToken(authToken, userDetails))
            	return;
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            		userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            logger.info("authenticated user " + e + ", setting security context");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        chain.doFilter(request, response);
    }
}
