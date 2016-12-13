package com.govac.institutii;

import com.govac.institutii.db.Application;
import com.govac.institutii.db.ApplicationRepository;
import com.govac.institutii.db.Provider;
import com.govac.institutii.db.ProviderRepository;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;
import com.govac.institutii.validation.MessageDTO;
import com.govac.institutii.validation.MessageType;
import com.govac.institutii.validation.ApplicationAdminDTO;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.govac.institutii.security.JwtTokenUtil;

@RestController
@RequestMapping("/api/applications")
public class ApplicationRestController {
    @Autowired
    private MessageSource msgSource;

    @Autowired
    private ApplicationRepository appRepo;

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
    public ResponseEntity<?> getApps(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        Optional<String> email = jwtTokenUtil.getSubjectFromToken(token);
        return email
                .map((e) -> {
                    JwtUser usr = (JwtUser) userDetailsService
                            .loadUserByUsername(e);
                    if (null == usr) {
                        return ResponseEntity.status(403).body(null);
                    }

                    if (usr.getUser().getRole().equals("ROLE_ADMIN")) {
                        return ResponseEntity.ok(
                                appRepo.findAll(
                                        new PageRequest(page, size)
                                )
                        );
                    }
                    return ResponseEntity.ok(
                            appRepo.findByAdminEmail(
                                    e, new PageRequest(page, size)
                            )
                    );
                })
                .orElse(ResponseEntity.status(403).body(null));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN') || hasRole('PROVIDER')")
    public ResponseEntity<?> createApp(
            @RequestBody @Validated ApplicationAdminDTO app,
            HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        Optional<String> email = jwtTokenUtil.getSubjectFromToken(token);
        if (!email.isPresent()) {
            return ResponseEntity.badRequest().body(
                    new MessageDTO(
                            MessageType.ERROR,
                            translate("error.application.provider.nojwt")
                    )
            );
        }
        JwtUser usr = (JwtUser) userDetailsService
                .loadUserByUsername(email.get());
        if (null == usr) {
            return ResponseEntity.badRequest().body(
                    new MessageDTO(
                            MessageType.ERROR,
                            translate("error.application.provider.nojwt")
                    )
            );
        }
        Boolean isAdmin = usr.getUser().getRole().equals("ROLE_ADMIN");
        
        Provider loadedProvider = providerRepo.findOne(app.provider);
        if (null == loadedProvider) {
            return ResponseEntity.badRequest().body(
                    new MessageDTO(
                            MessageType.ERROR,
                            translate("error.application.provider.noentity")
                    )
            );
        }
        if (!isAdmin && !Objects.equals(
                loadedProvider.admin.getId(), usr.getUser().getId()
        )) {
            return ResponseEntity.badRequest().body(
                new MessageDTO(
                        MessageType.ERROR,
                        translate("error.application.provider.ownermismatch")
                )
            );
        }
        
        // token claims
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtTokenUtil.CLAIM_KEY_ISSUER, "GovacInstitutii");
        claims.put(JwtTokenUtil.CLAIM_KEY_SUBJECT, app.provider);
        claims.put(JwtTokenUtil.CLAIM_KEY_AUDIENCE, JwtTokenUtil.AUDIENCE_WEB);
        claims.put(JwtTokenUtil.CLAIM_KEY_CREATED, jwtTokenUtil.generateCreationDate());
        claims.put(JwtTokenUtil.CLAIM_KEY_EXPIRES, jwtTokenUtil.generateExpirationDate());
        Application toSaveApp = new Application(
                loadedProvider, app.name, app.description, 
                jwtTokenUtil.generateToken(claims), 
                app.requirements.toString()
        );
        Application savedApp = appRepo.save(toSaveApp);
        return ResponseEntity.ok(savedApp);
    }

    private String translate(String m) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return msgSource.getMessage(m, null, currentLocale);
    }
}
