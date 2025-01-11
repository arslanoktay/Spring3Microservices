package com.oa.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

// entegration tests

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //8080 yerine farklı bir portta çalışması için Random_port eklendi!
class ProductServiceApplicationTests {

	@ServiceConnection  // manuel olarak mongodbcontainer uri vermek yerine oto buraya getirecek
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	// application çalıştığında port u buna atayacak
	@LocalServerPort
	private Integer port;

	//io.rest-assured dependency'i pom.xml'e ekledik
	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost"; // port oto gelecek
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct() {
		// 3" java 14 ile geldi böylece kolaylıkla json larımızı yazıyoruz
		// Çıktı bı olarak kontrol edilecek
		String requestBody = """ 
				{
				    "name": "iPhone 15",
				    "description": "iPhone 15 is a smartphone from Apple",
				    "price": 1000
				}
				""";

		// test bunda gerçekleşecek beklenen sonuç, mesaj tipi gönderilecek veriler gibi içerikleri yazıyoruz
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("iPhone 15"))
				.body("description",Matchers.equalTo("iPhone 15 is a smartphone from Apple"))
				.body("price", Matchers.equalTo(1000));
	}

}


