package com.surepay.framework.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import com.surepay.framework.config.ApiEndpoints;
import com.surepay.framework.models.User;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.Matchers.*;

@Slf4j
public class UserService extends BaseService {
    
    @Override
    protected String getBasePath() {
        return ApiEndpoints.USERS;
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
    public Optional<User> findUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            log.warn("Username cannot be null or empty");
            return Optional.empty();
        }
        
        log.info("Searching for user with username: {}", username);
        
        List<User> allUsers = getAllUsers();
        
        // Exact match first
        Optional<User> exactMatch = allUsers.stream()
            .filter(user -> username.equals(user.getUsername()))
            .findFirst();
        
        if (exactMatch.isPresent()) {
            log.info("Found exact username match: {}", exactMatch.get().getUsername());
            return exactMatch;
        }
        
        // Case-insensitive match
        Optional<User> caseInsensitiveMatch = allUsers.stream()
            .filter(user -> username.equalsIgnoreCase(user.getUsername()))
            .findFirst();
        
        if (caseInsensitiveMatch.isPresent()) {
            log.info("Found case-insensitive username match: {}", 
                caseInsensitiveMatch.get().getUsername());
            return caseInsensitiveMatch;
        }
        
        log.warn("No user found with username: {}", username);
        return Optional.empty();
    }

}
