package com.surepay.framework.services;
import java.util.List;
import java.util.stream.Collectors;

import com.surepay.framework.config.ApiEndpoints;
import com.surepay.framework.models.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostService extends BaseService {

	@Override
	protected String getBasePath() {
		return ApiEndpoints.POSTS;
	}

	@Override
	public boolean isServiceHealthy() {
		try {
			getRequestSpec().when().get().then().statusCode(200);

			log.info("Post service health check passed");
			return true;
		} catch (Exception e) {
			log.error("Post service health check failed", e);
			return false;
		}
	}
	public List<Post> getPostsByUserId(Integer userId) {
		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("User ID must be positive integer");
		}
		log.info("Fetching posts for user ID: {}", userId);

		Response response = getRequestSpec().queryParam("userId", userId).when().get().then().statusCode(200)
				.contentType(ContentType.JSON).extract().response();

		List<Post> posts = response.jsonPath().getList(".", Post.class);

		// Additional validation - ensure all posts belong to the requested user
		List<Post> validPosts = posts.stream().filter(post -> post.belongsToUser(userId)).collect(Collectors.toList());

		if (validPosts.size() != posts.size()) {
			log.warn("Found {} posts not belonging to user {}", posts.size() - validPosts.size(), userId);
		}

		log.info("Successfully retrieved {} posts for user {}", validPosts.size(), userId);

		validatePostList(validPosts);

		return validPosts;
	}
	public List<Post> getPostsByUser(User user) {
		if (user == null || user.getId() == null) {
			throw new IllegalArgumentException("User or User ID cannot be null");
		}
		return getPostsByUserId(user.getId());
	}
	private void validatePostList(List<Post> posts) {
		if (posts == null) {
			throw new IllegalStateException("Post list cannot be null");
		}

		long invalidPosts = posts.stream().filter(post -> !post.isValidPost()).count();

		if (invalidPosts > 0) {
			log.warn("Found {} invalid posts in the response", invalidPosts);
		}

		log.info("Post list validation completed. Total: {}, Invalid: {}", posts.size(), invalidPosts);
	}

}
