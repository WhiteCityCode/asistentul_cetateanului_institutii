package com.govac.institutii;

import com.govac.institutii.db.Provider;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.govac.institutii.db.ProviderRepository;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/providers")
public class ProviderRestController {

    @Autowired
    private ProviderRepository providerRepo;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN') || hasRole('PROVIDER')")
    public ResponseEntity<?> getProviders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        Optional<String> email = jwtTokenUtil.getEmailFromToken(token);
        return email
                .map((e) -> {
                    JwtUser usr = (JwtUser) userDetailsService
                            .loadUserByUsername(e);
                    if (null == usr) {
                        return ResponseEntity.status(403).body(null);
                    }

                    if (usr.getUser().getRole().equals("ROLE_ADMIN")) {
                        return ResponseEntity.ok(
                                providerRepo.findAll(
                                        new PageRequest(page, size)
                                )
                        );
                    }
                    return ResponseEntity.ok(
                            providerRepo.findByAdminEmail(
                                    e, new PageRequest(page, size)
                            )
                    );
                })
                .orElse(ResponseEntity.status(403).body(null));
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN') || hasRole('PROVIDER')")
    public Provider createProvider(
            @RequestBody @Validated Provider provider,
            BindingResult result){
        provider = providerRepo.save(provider);
        return provider;
    }
}
