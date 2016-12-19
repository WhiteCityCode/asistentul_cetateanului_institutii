package com.govac.institutii;

import com.govac.institutii.db.Application;
import com.govac.institutii.db.ApplicationRepository;
import com.govac.institutii.db.Provider;
import com.govac.institutii.db.ProviderRepository;
import com.govac.institutii.db.User;
import com.govac.institutii.db.UserRepository;
import com.govac.institutii.security.JwtTokenUtil;
import com.govac.institutii.security.JwtUser;
import com.govac.institutii.security.JwtUserDetailsService;
import com.govac.institutii.validation.ApplicationAdminDTO;
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
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.hasItems;
import org.springframework.core.env.Environment;
import org.springframework.mobile.device.Device;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationControllerTest {

    private User providerUser;
    private User adminUser;
    private User extraProviderUser;
    private Provider defaultProvider;
    private Provider extraProvider;
    
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
        extraProviderUser = userRepository.save(toSaveExtraProvider);
        
        defaultProvider = providerRepository.save(
                new Provider(providerUser, "MAI", "http://mai.gov.ro")
        );        
        extraProvider = providerRepository.save(new Provider(
                extraProviderUser,
                "Ministerul Sanatatii",
                "http://www.ms.ro"
        ));
        
        List<String> amberReqs = new ArrayList();
        amberReqs.add("cnp");
        appRepository.save(
                new Application(
                        defaultProvider, "AMBER", "Minori disparuti"
                        , "some-token1", amberReqs
                )
        );
        
        List<String> drugListReqs = new ArrayList();
        drugListReqs.add("email");
        appRepository.save(
                new Application(
                        extraProvider, "Compensate"
                        , "Lista medicamente compensate", "some-token2"
                        , drugListReqs
                )
        );
    }

    @After
    public void cleanUp() {
        appRepository.deleteAll();
        providerRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    public void shouldGetAllApplicationsAsAdmin() 
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
            get("/api/applications").
            then().
            statusCode(HttpStatus.SC_OK).
            body("totalElements", is(2)).
            body("first", is(true)).
            body("last", is(true)).
            body("totalPages", is(1)).
            body(
                    "content.tkn", 
                    hasItems("some-token1", "some-token2")
            );
    }
    
    @Test
    public void shouldGetOwnAppsAsProvider()
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
                get("/api/applications").
                then().
                statusCode(HttpStatus.SC_OK).
                body("totalElements", is(1)).
                body("first", is(true)).
                body("last", is(true)).
                body("totalPages", is(1)).
                body(
                        "content.tkn",
                        hasItems("some-token1")
                );
    }

    @Test
    public void shouldSaveAppAsAdmin() 
            throws JsonGenerationException, JsonMappingException, IOException {
        List<String> postAppReqs = new ArrayList();
        postAppReqs.add("cnp");
        ApplicationAdminDTO postApp = new ApplicationAdminDTO(
                defaultProvider.getId(), "New app 1"
                , "Some new app 1 desc", postAppReqs
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
            body(objectMapper.writeValueAsString(postApp)).
            when().
            post("/api/applications").
            then().
            statusCode(HttpStatus.SC_OK).
            body("name", is("New app 1"));
    }
    
    @Test
    public void shouldNotSaveOnMismatchAsProvider()
            throws JsonGenerationException, JsonMappingException, IOException {
        List<String> postAppReqs = new ArrayList();
        postAppReqs.add("cnp");
        ApplicationAdminDTO postApp = new ApplicationAdminDTO(
                extraProvider.getId(), "New app 2"
                , "Some new app 2 desc", postAppReqs
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
            body(objectMapper.writeValueAsString(postApp)).
            when().
            post("/api/applications").
            then().
            statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}