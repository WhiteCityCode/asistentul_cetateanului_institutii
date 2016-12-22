package com.govac.institutii;

import com.govac.institutii.db.ApplicationRepository;
import com.govac.institutii.db.ProviderRepository;
import com.govac.institutii.db.User;
import com.govac.institutii.db.UserRepository;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import static org.hamcrest.CoreMatchers.hasItems;
import org.springframework.core.env.Environment;
import org.springframework.mobile.device.Device;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private User providerUser;
    private User adminUser;
    
    @Autowired
    private Environment env;

    @Autowired
    public UserRepository userRepository;
    
    @Autowired
    public ProviderRepository providerRepository;
    
    @Autowired
    public ApplicationRepository appRepository;

    @Value("${local.server.port}")
    int port;
    
//    @Value("${jwt.header}")
//    private String tokenHeader;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private String getAuthToken(String email, Device device) {
        final JwtUser userDetails = (JwtUser) userDetailsService
                .loadUserByUsername(email);
        return jwtTokenUtil.generateToken(userDetails, device);
    }

    @Before
    public void setup() {
        RestAssured.port = port;
        cleanUp();
        adminUser = new User(
                "1231231231122", "user1@email.com", "user1FN", "user1LN"
                , "123456"                 
        );
        adminUser.setRole("ROLE_ADMIN");
        userRepository.save(adminUser);
        providerUser = new User(
                "2345677654433", "user2@email.com", "user2FN", "user2LN"
                , "345678"                 
        );
        providerUser.setRole("ROLE_PROVIDER");
        userRepository.save(providerUser);
    }

    @After
    public void cleanUp() {
        appRepository.deleteAll();
        providerRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    public void shouldGetPaginatedUsersAsAdmin() 
            throws JsonGenerationException, JsonMappingException, IOException {
        given().
            contentType(ContentType.JSON).
            filter((requestSpec, responseSpec, ctx) -> {
                requestSpec.header(
                        env.getProperty("jwt.header"), 
                        getAuthToken(adminUser.getEmail(),null)
                );
                return ctx.next(requestSpec, responseSpec);
            }).
            when().
            get("/api/users").
            then().
            statusCode(HttpStatus.SC_OK).
            body("totalElements", is(2)).
            body("first", is(true)).
            body("last", is(true)).
            body("totalPages", is(1)).
            body(
                    "content.email", 
                    hasItems("user1@email.com", "user2@email.com")
            );
    }

    @Test
    public void shouldSaveUserAsAdmin() 
            throws JsonGenerationException, JsonMappingException, IOException {
        User postUser = new User(
                "2345677654434", "user3@email.com",
                 "user3FN", "user3LN", "3479"
        );
        postUser.setRole("ROLE_PROVIDER");
        given().
            contentType(ContentType.JSON).
            filter((requestSpec, responseSpec, ctx) -> {
                requestSpec.header(
                        env.getProperty("jwt.header"),
                        getAuthToken(adminUser.getEmail(), null)
                );
                return ctx.next(requestSpec, responseSpec);
            }).
            body(objectMapper.writeValueAsString(postUser)).
            when().
            post("/api/users").
            then().
            statusCode(HttpStatus.SC_OK).
            body("email", is("user3@email.com"));
    }
    
    @Test
    public void shouldNotSaveUserAsNotAdmin()
            throws JsonGenerationException, JsonMappingException, IOException {
        User postUser = new User(
                "2345677654435", "user4@email.com",
                "user4FN", "user4LN", "3481"
        );
        postUser.setRole("ROLE_PROVIDER");
        given().
                contentType(ContentType.JSON).
                filter((requestSpec, responseSpec, ctx) -> {
                    requestSpec.header(
                            env.getProperty("jwt.header"),
                            getAuthToken(providerUser.getEmail(), null)
                    );
                    return ctx.next(requestSpec, responseSpec);
                }).
                body(objectMapper.writeValueAsString(postUser)).
                when().
                post("/api/users").
                then().
                statusCode(HttpStatus.SC_FORBIDDEN);
    }
}
