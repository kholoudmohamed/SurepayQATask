package com.surepay.framework.models;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
	@JsonProperty("id")
	private Integer id;

	@JsonProperty("postId")
	private Integer postId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("body")
	private String body;

	public boolean isValidComment() {
		return id != null && id > 0 && postId != null && postId > 0 && name != null && !name.trim().isEmpty()
				&& email != null && !email.trim().isEmpty() && body != null && !body.trim().isEmpty();
	}

	private static final Pattern EMAIL_PATTERN = Pattern
			.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

	public boolean hasValidEmailFormat() {
		return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
	}

	public boolean belongsToPost(Integer targetPostId) {
		return postId != null && postId.equals(targetPostId);
	}
}
