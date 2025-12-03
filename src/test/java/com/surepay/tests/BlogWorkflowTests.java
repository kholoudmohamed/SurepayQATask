package com.surepay.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.surepay.framework.models.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlogWorkflowTests extends BaseTest {

    private static final String TARGET_USERNAME = "Delphine";

    @Test(groups = {"regression"}, priority = 1)
    public void userBlogSearchWorflow() {

        var selectedUser = userService.findUserByUsername(TARGET_USERNAME).orElse(null);
        var userPosts = postService.getPostsByUser(selectedUser);
        for (Post post : userPosts) {
            var postComments = commentService.getCommentsByPost(post);
            for (Comment comment : postComments) {
                Assert.assertTrue(comment.hasValidEmailFormat(),
                    String.format("Comment ID %d has invalid email format: %s",
                        comment.getId(), comment.getEmail()));
            }   
        }

    }
}