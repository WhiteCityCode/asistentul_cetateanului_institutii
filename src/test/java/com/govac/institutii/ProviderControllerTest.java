package com.govac.institutii;

import com.govac.institutii.db.ApplicationRepository;
import com.govac.institutii.db.Provider;
import com.govac.institutii.db.ProviderRepository;
import com.govac.institutii.db.User;
import com.govac.institutii.db.UserRepository;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;
import com.govac.institutii.validation.ProviderAdminDTO;
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
public class ProviderControllerTest {

    private User providerUser;
    private User adminUser;
    private Provider defaultProvider;
    
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
        User toSaveAdminUser = new User(
                "1231231231122", "user1@email.com", "user1FN", "user1LN"
                , "123456"                 
        );
        toSaveAdminUser.setRole("ROLE_ADMIN");
        adminUser = userRepository.save(toSaveAdminUser);
        
        User toSaveProviderUser = new User(
                "2345677654433", "user2@email.com", "user2FN", "user2LN"
                , "345678"                 
        );
        toSaveProviderUser.setRole("ROLE_PROVIDER");
        providerUser = userRepository.save(toSaveProviderUser);
        
        User toSaveExtraProvider = new User(
                "1231231231123", "user3@email.com", "user3FN"
                , "user3LN", "123457"
        );
        toSaveExtraProvider.setRole("ROLE_PROVIDER");
        User extraProvider = userRepository.save(toSaveExtraProvider);
        
        defaultProvider = providerRepository.save(
                new Provider(providerUser, "MAI", "http://mai.gov.ro")
        );
        
        providerRepository.save(new Provider(
                extraProvider,
                "Ministerul Sanatatii",
                "http://www.ms.ro"
        ));
    }

    @After
    public void cleanUp() {
        appRepository.deleteAll();
        providerRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    public void shouldGetAllProvidersAsAdmin() 
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
            get("/api/providers").
            then().
            statusCode(HttpStatus.SC_OK).
            body("totalElements", is(2)).
            body("first", is(true)).
            body("last", is(true)).
            body("totalPages", is(1)).
            body(
                    "content.url", 
                    hasItems("http://mai.gov.ro", "http://www.ms.ro")
            );
    }
    
    @Test
    public void shouldGetOwnProvidersAsProvider()
            throws JsonGenerationException, JsonMappingException, IOException {
        given().
                contentType(ContentType.JSON).
                filter((requestSpec, responseSpec, ctx) -> {
                    requestSpec.header(
                            env.getProperty("jwt.header"),
                            getAuthToken(providerUser.getEmail(), null)
                    );
                    return ctx.next(requestSpec, responseSpec);
                }).
                when().
                get("/api/providers").
                then().
                statusCode(HttpStatus.SC_OK).
                body("totalElements", is(1)).
                body("first", is(true)).
                body("last", is(true)).
                body("totalPages", is(1)).
                body(
                        "content.url",
                        hasItems("http://mai.gov.ro")
                );
    }

    @Test
    public void shouldSaveProviderAsAdmin() 
            throws JsonGenerationException, JsonMappingException, IOException {
        ProviderAdminDTO postProvider = new ProviderAdminDTO(
                providerUser.getId(), "New provider"
                , "http://adminprovider.com"
        );
        given().
            contentType(ContentType.JSON).
            filter((requestSpec, responseSpec, ctx) -> {
                requestSpec.header(
                        env.getProperty("jwt.header"),
                        getAuthToken(adminUser.getEmail(), null)
                );
                return ctx.next(requestSpec, responseSpec);
            }).
            body(objectMapper.writeValueAsString(postProvider)).
            when().
            post("/api/providers").
            then().
            statusCode(HttpStatus.SC_OK).
            body("admin.email", is(providerUser.getEmail()));
    }
    
    @Test
    public void shouldSaveOwnProviderAsProvider()
            throws JsonGenerationException, JsonMappingException, IOException {
        ProviderAdminDTO postProvider = new ProviderAdminDTO(
                adminUser.getId(), "New p provider",
                 "http://pprovider.com"
        );
        given().
                contentType(ContentType.JSON).
                filter((requestSpec, responseSpec, ctx) -> {
                    requestSpec.header(
                            env.getProperty("jwt.header"),
                            getAuthToken(providerUser.getEmail(), null)
                    );
                    return ctx.next(requestSpec, responseSpec);
                }).
                body(objectMapper.writeValueAsString(postProvider)).
                when().
                post("/api/providers").
                then().
                statusCode(HttpStatus.SC_OK).
                body("admin.email", is(providerUser.getEmail()));
    }
}