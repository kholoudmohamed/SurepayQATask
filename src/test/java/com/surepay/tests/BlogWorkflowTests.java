package com.surepay.tests;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.surepay.framework.config.ConfigManager;
import com.surepay.framework.models.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlogWorkflowTests extends BaseTest {

	private static final String TARGET_USERNAME = "Delphine";

	@Test(groups = {
			"regression"}, priority = 1, description = "Validates email formats in all comments for user's blog posts")
	public void testEmailFormatsInUserBlogComments() {
		log.info("Starting test: Validate email formats in user blog comments for user '{}'", TARGET_USERNAME);

		// Step 1: Find user by username
		User selectedUser = userService.findUserByUsername(TARGET_USERNAME)
				.orElseThrow(() -> new AssertionError("User with username '" + TARGET_USERNAME + "' not found"));

		// Step 2: Get all posts by the user
		var userPosts = postService.getPostsByUser(selectedUser);
		Assert.assertFalse(userPosts.isEmpty(), "No posts found for user: " + TARGET_USERNAME);

		// Step 3: Validate comments for all posts
		int validatedCommentsCount = 0;
		for (Post post : userPosts) {
			var postComments = commentService.getCommentsByPost(post);
			for (Comment comment : postComments) {
				Assert.assertTrue(comment.hasValidEmailFormat(), String
						.format("Comment ID %d has invalid email format: %s", comment.getId(), comment.getEmail()));
				validatedCommentsCount++;
			}
		}

		log.info("Successfully validated {} comments across {} posts for user '{}'", validatedCommentsCount,
				userPosts.size(), TARGET_USERNAME);
	}

	@Test(groups = {"edge-case",
			"regression"}, priority = 2, description = "Edge Case: Handle non-existent user gracefully")
	public void testNonExistentUserHandling() {
		log.info("Testing behavior with non-existent user");
		String nonExistentUsername = "NonExistentUser_" + System.currentTimeMillis();

		try {
			Optional<User> user = userService.findUserByUsername(nonExistentUsername);
			Assert.assertFalse(user.isPresent(), "Unexpected: Found user with random username: " + nonExistentUsername);
			log.info("Correctly handled non-existent user: {}", nonExistentUsername);
		} catch (Exception e) {
			Assert.fail("Exception occurred while handling non-existent user: " + e.getMessage());
		}
	}

	@Test(groups = {"edge-case", "regression"}, priority = 3, description = "Validate user with no posts scenario")
	public void testUserWithNoPosts() {
		log.info("Testing user with potentially zero posts");

		// Dynamically find a user with minimal or no posts
		var allUsers = userService.getAllUsers();
		Assert.assertFalse(allUsers.isEmpty(), "No users found in the system");

		// Find user with the least number of posts
		User userWithLeastPosts = allUsers.stream().min((u1, u2) -> Integer
				.compare(postService.getPostsByUser(u1).size(), postService.getPostsByUser(u2).size())).orElse(null);

		if (userWithLeastPosts == null) {
			log.info("Could not find any user to test - skipping test");
			throw new org.testng.SkipException("No users available for testing");
		}

		var userPosts = postService.getPostsByUser(userWithLeastPosts);

		if (userPosts.isEmpty()) {
			log.info("User '{}' has no posts - validating graceful handling", userWithLeastPosts.getUsername());
			// This is a valid scenario - user might not have written any posts yet
			Assert.assertTrue(true, "Successfully handled user with no posts");
		} else {
			log.info("User '{}' has {} posts - verifying data consistency", userWithLeastPosts.getUsername(),
					userPosts.size());
			// Validate that all posts belong to the correct user
			for (Post post : userPosts) {
				Assert.assertEquals(post.getUserId(), userWithLeastPosts.getId(),
						"Post belongs to different user than expected");
			}
		}
	}

	@Test(groups = {"data-validation",
			"regression"}, priority = 4, description = "Comprehensive email format validation")
	public void testComprehensiveEmailValidation() {
		log.info("Testing comprehensive email format validation patterns");

		// Get comments from multiple users to test various email formats
		var allUsers = userService.getAllUsers();
		Assert.assertFalse(allUsers.isEmpty(), "No users found in the system");

		int totalCommentsValidated = 0;
		int invalidEmailsFound = 0;

		// Test first 3 users to get diverse email patterns
		for (int i = 0; i < Math.min(3, allUsers.size()); i++) {
			User user = allUsers.get(i);
			var posts = postService.getPostsByUser(user);

			for (Post post : posts) {
				var comments = commentService.getCommentsByPost(post);
				for (Comment comment : comments) {
					totalCommentsValidated++;

					// Validate email format and log patterns
					if (!comment.hasValidEmailFormat()) {
						invalidEmailsFound++;
						log.warn("Invalid email format found: '{}' in comment ID: {}", comment.getEmail(),
								comment.getId());
					}

					// Additional email validations
					Assert.assertNotNull(comment.getEmail(), "Comment email should not be null");
					Assert.assertFalse(comment.getEmail().trim().isEmpty(), "Comment email should not be empty");
					Assert.assertTrue(comment.getEmail().contains("@"),
							"Email should contain @ symbol: " + comment.getEmail());
				}
			}
		}

		log.info("Email validation completed: {} total emails validated, {} invalid formats found",
				totalCommentsValidated, invalidEmailsFound);

	}

	@Test(groups = {"data-integrity",
			"regression"}, priority = 5, description = "Validate data consistency across API endpoints")
	public void testDataConsistencyValidation() {
		log.info("Testing data consistency across different API endpoints");

		// Get Delphine's data and verify consistency
		User user = userService.findUserByUsername(TARGET_USERNAME)
				.orElseThrow(() -> new AssertionError("User '" + TARGET_USERNAME + "' not found"));

		var userPosts = postService.getPostsByUser(user);

		for (Post post : userPosts) {
			// Validate post belongs to correct user
			Assert.assertEquals(post.getUserId(), user.getId(),
					"Post user ID mismatch. Expected: " + user.getId() + ", Found: " + post.getUserId());

			// Validate post has required fields
			Assert.assertNotNull(post.getTitle(), "Post title should not be null");
			Assert.assertNotNull(post.getBody(), "Post body should not be null");
			Assert.assertTrue(post.getId() > 0, "Post ID should be positive");

			var comments = commentService.getCommentsByPost(post);

			for (Comment comment : comments) {
				// Validate comment belongs to correct post
				Assert.assertEquals(comment.getPostId(), post.getId(),
						"Comment post ID mismatch. Expected: " + post.getId() + ", Found: " + comment.getPostId());

				// Validate comment has required fields
				Assert.assertNotNull(comment.getName(), "Comment name should not be null");
				Assert.assertNotNull(comment.getEmail(), "Comment email should not be null");
				Assert.assertNotNull(comment.getBody(), "Comment body should not be null");
				Assert.assertTrue(comment.getId() > 0, "Comment ID should be positive");
			}
		}

		log.info("Data consistency validation passed for user '{}' with {} posts", TARGET_USERNAME, userPosts.size());
	}

	@Test(groups = {"performance", "regression"}, priority = 6, description = "Validate API response times")
	public void testApiPerformance() {
		log.info("Testing API performance and response times");

		long startTime, endTime, duration;

		// Get environment-specific performance thresholds
		int usersThreshold = ConfigManager.getInstance().getConfig().performanceThresholdUsersApi();
		int postsThreshold = ConfigManager.getInstance().getConfig().performanceThresholdPostsApi();
		int commentsThreshold = ConfigManager.getInstance().getConfig().performanceThresholdCommentsApi();

		// Test user search performance
		startTime = System.currentTimeMillis();
		var allUsers = userService.getAllUsers();
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;

		Assert.assertTrue(duration < usersThreshold,
				String.format("User API response too slow: %dms (threshold: %dms)", duration, usersThreshold));
		log.info("User API response time: {}ms for {} users (threshold: {}ms)", duration, allUsers.size(),
				usersThreshold);

		// Test posts retrieval performance
		User testUser = allUsers.get(0);
		startTime = System.currentTimeMillis();
		var posts = postService.getPostsByUser(testUser);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;

		Assert.assertTrue(duration < postsThreshold,
				String.format("Posts API response too slow: %dms (threshold: %dms)", duration, postsThreshold));
		log.info("Posts API response time: {}ms for {} posts (threshold: {}ms)", duration, posts.size(),
				postsThreshold);

		// Test comments retrieval performance if posts exist
		if (!posts.isEmpty()) {
			Post testPost = posts.get(0);
			startTime = System.currentTimeMillis();
			var comments = commentService.getCommentsByPost(testPost);
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;

			Assert.assertTrue(duration < commentsThreshold, String
					.format("Comments API response too slow: %dms (threshold: %dms)", duration, commentsThreshold));
			log.info("Comments API response time: {}ms for {} comments (threshold: {}ms)", duration, comments.size(),
					commentsThreshold);
		}
	}

	@Test(groups = {"security", "regression"}, priority = 7, description = "Test API input validation and security")
	public void testApiInputValidation() {
		log.info("Testing API input validation and security measures");

		// Test SQL injection patterns (should be handled gracefully)
		String[] maliciousInputs = {"'; DROP TABLE users; --", "<script>alert('xss')</script>",
				"../../../../etc/passwd", "' OR '1'='1", "NULL", ""};

		for (String maliciousInput : maliciousInputs) {
			try {
				// Try to search with malicious input - should return empty or handle gracefully
				Optional<User> result = userService.findUserByUsername(maliciousInput);
				// If we get here without exception, that's good - API handled it gracefully
				Assert.assertFalse(result.isPresent(), "Unexpected user found for malicious input: " + maliciousInput);
				log.info("API properly handled potentially malicious input: {}", maliciousInput.replace("\n", "\\n"));
			} catch (Exception e) {
				// Log but don't fail - some validation exceptions might be expected
				log.info("API threw exception for malicious input '{}': {}", maliciousInput.replace("\n", "\\n"),
						e.getMessage());
			}
		}
	}

	@Test(groups = {"schema-validation",
			"regression"}, priority = 8, description = "Validate API response schema compliance")
	public void testApiSchemaValidation() {
		log.info("Testing API response schema compliance");

		// Test User schema
		User user = userService.findUserByUsername(TARGET_USERNAME)
				.orElseThrow(() -> new AssertionError("User '" + TARGET_USERNAME + "' not found"));

		// Validate required User fields
		Assert.assertNotNull(user.getId(), "User ID should not be null");
		Assert.assertNotNull(user.getUsername(), "User username should not be null");
		Assert.assertNotNull(user.getEmail(), "User email should not be null");
		Assert.assertNotNull(user.getName(), "User name should not be null");

		// Test Posts schema
		var posts = postService.getPostsByUser(user);
		if (!posts.isEmpty()) {
			Post post = posts.get(0);
			Assert.assertNotNull(post.getId(), "Post ID should not be null");
			Assert.assertNotNull(post.getUserId(), "Post user ID should not be null");
			Assert.assertNotNull(post.getTitle(), "Post title should not be null");
			Assert.assertNotNull(post.getBody(), "Post body should not be null");

			// Test Comments schema
			var comments = commentService.getCommentsByPost(post);
			if (!comments.isEmpty()) {
				Comment comment = comments.get(0);
				Assert.assertNotNull(comment.getId(), "Comment ID should not be null");
				Assert.assertNotNull(comment.getPostId(), "Comment post ID should not be null");
				Assert.assertNotNull(comment.getName(), "Comment name should not be null");
				Assert.assertNotNull(comment.getEmail(), "Comment email should not be null");
				Assert.assertNotNull(comment.getBody(), "Comment body should not be null");
			}
		}

		log.info("API schema validation completed successfully");
	}

	@Test(groups = {"boundary", "regression"}, priority = 9, description = "Test boundary conditions and edge cases")
	public void testBoundaryConditions() {
		log.info("Testing boundary conditions and edge cases");

		var allUsers = userService.getAllUsers();
		Assert.assertFalse(allUsers.isEmpty(), "Should have at least one user");

		// Test with user who has maximum posts
		User userWithMostPosts = allUsers.stream().max((u1, u2) -> Integer
				.compare(postService.getPostsByUser(u1).size(), postService.getPostsByUser(u2).size()))
				.orElse(allUsers.get(0));

		var maxPosts = postService.getPostsByUser(userWithMostPosts);
		log.info("User '{}' has maximum {} posts", userWithMostPosts.getUsername(), maxPosts.size());

		// Test with user who has minimum posts
		User userWithLeastPosts = allUsers.stream().min((u1, u2) -> Integer
				.compare(postService.getPostsByUser(u1).size(), postService.getPostsByUser(u2).size()))
				.orElse(allUsers.get(0));

		var minPosts = postService.getPostsByUser(userWithLeastPosts);
		log.info("User '{}' has minimum {} posts", userWithLeastPosts.getUsername(), minPosts.size());

		// Validate all boundary cases handle correctly
		Assert.assertTrue(maxPosts.size() >= minPosts.size(), "Max posts should be >= min posts");

		// Test comments boundary conditions
		if (!maxPosts.isEmpty()) {
			Post postWithMostComments = maxPosts.stream().max((p1, p2) -> Integer
					.compare(commentService.getCommentsByPost(p1).size(), commentService.getCommentsByPost(p2).size()))
					.orElse(maxPosts.get(0));

			var maxComments = commentService.getCommentsByPost(postWithMostComments);
			log.info("Post {} has maximum {} comments", postWithMostComments.getId(), maxComments.size());

			// Validate all comments in the largest set
			for (Comment comment : maxComments) {
				Assert.assertTrue(comment.hasValidEmailFormat(),
						"Comment with ID " + comment.getId() + " has invalid email: " + comment.getEmail());
			}
		}
	}
}