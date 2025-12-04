package com.surepay.framework.config;

public final class ApiEndpoints {

	// Resource endpoints
	public static final String USERS = "/users";
	public static final String POSTS = "/posts";
	public static final String COMMENTS = "/comments";

	// Nested resource patterns
	public static final String USER_POSTS = "/users/{userId}/posts";

	private ApiEndpoints() {
		// Utility class - no instantiation
	}
}
