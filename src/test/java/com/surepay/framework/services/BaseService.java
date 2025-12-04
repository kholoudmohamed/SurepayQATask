package com.surepay.framework.services;

import com.surepay.framework.config.ConfigManager;
import com.surepay.framework.config.TestConfig;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseService {

	protected final TestConfig config;
	protected final RequestSpecification requestSpec;
	protected final ResponseSpecification responseSpec;

	protected BaseService() {
		this.config = ConfigManager.getInstance().getConfig();
		this.requestSpec = buildRequestSpecification();
		this.responseSpec = buildResponseSpecification();

		// Configure RestAssured defaults
		RestAssured.requestSpecification = requestSpec;
		RestAssured.responseSpecification = responseSpec;

		log.info("Initialized API client for: {}", this.getClass().getSimpleName());
	}
	protected RequestSpecification getRequestSpec() {
		return RestAssured.given(requestSpec).basePath(getBasePath());
	}
	private RequestSpecification buildRequestSpecification() {
		return new RequestSpecBuilder().setBaseUri(config.baseUrl()).setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON).build();
	}
	private ResponseSpecification buildResponseSpecification() {
		return new ResponseSpecBuilder().expectContentType(ContentType.JSON).build();
	}
	protected abstract String getBasePath();
	public abstract boolean isServiceHealthy();
}