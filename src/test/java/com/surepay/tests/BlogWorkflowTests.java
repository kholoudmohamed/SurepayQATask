package com.surepay.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.surepay.framework.models.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlogWorkflowTests extends BaseTest {

	private static final String TARGET_USERNAME = "Delphine";

	@Test(groups = {
			"regression"}, priority = 1, description = "Validates email formats in all comments for user's blog posts")
	public void validateEmailFormatsInUserBlogComments() {
		log.info("Starting test: Validate email formats in user blog comments for user '{}'", TARGET_USERNAME);

		User selectedUser = userService.findUserByUsername(TARGET_USERNAME)
				.orElseThrow(() -> new AssertionError("User with username '" + TARGET_USERNAME + "' not found"));

		var userPosts = postService.getPostsByUser(selectedUser);
		Assert.assertFalse(userPosts.isEmpty(), "No posts found for user: " + TARGET_USERNAME);

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
}