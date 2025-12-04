package com.surepay.framework.models;

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
public class User {
	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("username")
	private String username;

	@JsonProperty("email")
	private String email;

	@JsonProperty("address")
	private Address address;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("website")
	private String website;

	@JsonProperty("company")
	private Company company;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Address {

		@JsonProperty("street")
		private String street;

		@JsonProperty("suite")
		private String suite;

		@JsonProperty("city")
		private String city;

		@JsonProperty("zipcode")
		private String zipcode;

		@JsonProperty("geo")
		private Geo geo;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Geo {

			@JsonProperty("lat")
			private String lat;

			@JsonProperty("lng")
			private String lng;
		}
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Company {

		@JsonProperty("name")
		private String name;

		@JsonProperty("catchPhrase")
		private String catchPhrase;

		@JsonProperty("bs")
		private String bs;
	}

	public boolean isValidUser() {
		return id != null && id > 0 && username != null && !username.trim().isEmpty() && email != null
				&& isValidEmail(email);
	}
	private boolean isValidEmail(String email) {
		return email != null && email.contains("@") && email.contains(".") && !email.trim().isEmpty();
	}
}
