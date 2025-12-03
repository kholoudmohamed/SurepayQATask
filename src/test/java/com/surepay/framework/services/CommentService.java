package com.surepay.framework.services;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import com.surepay.framework.config.ApiEndpoints;
import com.surepay.framework.models.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

@Slf4j
public class CommentService extends BaseService {
    
    @Override
    protected String getBasePath() {
        return ApiEndpoints.COMMENTS;
    }

    @Override
    public boolean isServiceHealthy() {
        try {
            getRequestSpec()
                .when()
                .get()
                .then()
                .statusCode(200);

            log.info("CommentService health check passed");
            return true;
        } catch (Exception e) {
            log.error("CommentService health check failed: {}", e.getMessage());
            return false;
        }
    }
        public List<Comment> getCommentsByPostId(Integer postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Post ID must be positive integer");
        }

            log.info("Fetching comments for post ID: {}", postId);
            
            Response response = getRequestSpec()
                .queryParam("postId", postId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();
            
            List<Comment> comments = response.jsonPath().getList(".", Comment.class);
            
            // Validate all comments belong to the requested post
            List<Comment> validComments = comments.stream()
                .filter(comment -> comment.belongsToPost(postId))
                .collect(Collectors.toList());
            
            if (validComments.size() != comments.size()) {
                log.warn("Found {} comments not belonging to post {}", 
                    comments.size() - validComments.size(), postId);
            }
            
            log.info("Successfully retrieved {} comments for post {}", validComments.size(), postId);
            
            return validComments;
    }
    public List<Comment> getCommentsByPost(Post post) {
        if (post == null || post.getId() == null) {
            throw new IllegalArgumentException("Post or Post ID cannot be null");
        }
        return getCommentsByPostId(post.getId());
    }
    
}
