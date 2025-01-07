package com.oa.microservices.order;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;

// Integration Test
//@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");
	@LocalServerPort
	private Integer port;

	static { // static ctor, java da birden fazla blok açılabilir ve bunlar sırası ile çalışacaktır!!!
		mySQLContainer.start();
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void shouldSubmitOrder() {
		String submitJson = """
				{
				    "skuCode": "iPhone_15",
				    "price": 1000,
				    "quantity": 1
				}
				""";
		var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(submitJson)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body()
				.asString();

		assertThat(responseBodyString,Matchers.is("Order Placed Successfully"));
	}

}
