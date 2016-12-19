package com.govac.institutii.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    public static final String CLAIM_KEY_ISSUER="iss";
    public static final String CLAIM_KEY_SUBJECT= "sub";
    public static final String CLAIM_KEY_AUDIENCE = "aud";
    public static final String CLAIM_KEY_CREATED = "iat";
    public static final String CLAIM_KEY_EXPIRES = "exp";

    public static final String AUDIENCE_UNKNOWN = "unknown";
    public static final String AUDIENCE_WEB = "web";
    public static final String AUDIENCE_MOBILE = "mobile";
    public static final String AUDIENCE_TABLET = "tablet";    
 
    private final Log logger = LogFactory.getLog(this.getClass());

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public Optional<String> getSubjectFromToken(String token) {
        final Optional<Map<String, Object>> claims = getClaimsFromToken(token);
        return claims.map((c) -> {
        	return (String) c.get(CLAIM_KEY_SUBJECT);
        });
    }

    public Optional<Date> getCreatedDateFromToken(String token) {
    	final Optional<Map<String, Object>> claims = getClaimsFromToken(token);
        return claims.map((c) -> {
        	return new Date((Integer) c.get(CLAIM_KEY_CREATED) * 1000L);
        });
    }

    public Optional<Date> getExpirationDateFromToken(String token) {
    	final Optional<Map<String, Object>> claims = getClaimsFromToken(token);
        return claims.map((c) -> {
        	return new Date((Integer) c.get(CLAIM_KEY_EXPIRES) * 1000L);
        });
    }

    public Optional<String> getAudienceFromToken(String token) {
    	final Optional<Map<String, Object>> claims = getClaimsFromToken(token);
        return claims.map((c) -> {
        	return (String) c.get(CLAIM_KEY_AUDIENCE);
        });
    }

    private Optional<Map<String, Object>> getClaimsFromToken(String token) {
        Map<String, Object> claims = null;
        try {
        	final JWTVerifier verifier = new JWTVerifier(secret);
        	claims = verifier.verify(token);
        } catch (Exception e) {
        	return Optional.empty();
        }
        return Optional.of(claims);
    }
    
    public long generateCreationDate() {
        return (System.currentTimeMillis() / 1000L);
    }

    public long generateExpirationDate() {
        return (System.currentTimeMillis() / 1000L)  + expiration;
    }

    private Boolean isTokenExpired(String token) {
        final Optional<Date> expiration = getExpirationDateFromToken(token);
        return expiration
        	.map((d) -> {return d.before(new Date());})
        	.orElse(false);
    }

    public String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (null == device) {
            return audience;
        }
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        }
        return audience;
    }

    private Boolean ignoreTokenExpiration(String token) {
    	final Optional<String> audience = getAudienceFromToken(token);
        return audience
        	.map((a) -> {return AUDIENCE_TABLET.equals(a) || AUDIENCE_MOBILE.equals(a);})
        	.orElse(false);
    }

    public String generateToken(JwtUser userDetails, Device device) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ISSUER, "GovacInstitutii");
        claims.put(CLAIM_KEY_SUBJECT, userDetails.getEmail());
        claims.put(CLAIM_KEY_AUDIENCE, generateAudience(device));
        claims.put(CLAIM_KEY_CREATED, generateCreationDate());
        claims.put(CLAIM_KEY_EXPIRES, generateExpirationDate());
        logger.info("Generating token for secret " + secret + " and claims ");
        logger.info(claims);
        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
    	final JWTSigner signer = new JWTSigner(secret);
        return signer.sign(claims);
    }

    public Boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token) || ignoreTokenExpiration(token);
    }

    public Optional<String> refreshToken(String token) {
    	final Optional<Map<String, Object>> claims = getClaimsFromToken(token);
    	return claims
    		.map((c) -> {
    			c.put(CLAIM_KEY_EXPIRES, new Date()); 
    			return Optional.of(generateToken(c));
    		})
    		.orElse(Optional.empty());
    }

    public Boolean validateToken(String token, JwtUser user) {
        final Optional<String> email = getSubjectFromToken(token);
        return email
        	.map((e) -> {
        		return e.equals(user.getEmail()) && !isTokenExpired(token);
        	})
        	.orElse(false);
    }
}
