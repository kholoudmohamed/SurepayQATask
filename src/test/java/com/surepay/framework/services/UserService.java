package com.surepay.framework.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.surepay.framework.models.User;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.Matchers.*;

@Slf4j
public class UserService extends BaseService {
    
    private static final String USERS_ENDPOINT = "/users";
    
    @Override
    protected String getBasePath() {
        return USERS_ENDPOINT;
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users");
        
        Response response = getRequestSpec()
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(greaterThan(0)))
            .extract()
            .response();
        
        List<User> users = response.jsonPath().getList(".", User.class);
        log.info("Successfully retrieved {} users", users.size());
        
        // Validate data quality
        validateUserList(users);
        
        return users;
       
    }
    private void validateUserList(List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalStateException("User list is null or empty");
        }
        
        long invalidUsers = users.stream()
            .filter(user -> !user.isValidUser())
            .count();
        
        if (invalidUsers > 0) {
            log.warn("Found {} invalid users in the response", invalidUsers);
        }
        
        // Check for duplicate usernames
        long uniqueUsernames = users.stream()
            .map(User::getUsername)
            .distinct()
            .count();
        
        if (uniqueUsernames != users.size()) {
            log.warn("Duplicate usernames detected in user list");
        }
        
        log.info("User list validation completed. Total: {}, Invalid: {}", 
            users.size(), invalidUsers);
    }

    @Override
    public boolean isServiceHealthy() {
                try {
            getRequestSpec()
                .when()
                .get()
                .then()
                .statusCode(200);
            
            log.info("User service health check passed");
            return true;
        } catch (Exception e) {
            log.error("User service health check failed", e);
            return false;
        }
    }

}
