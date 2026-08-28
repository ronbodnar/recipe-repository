package com.ronbodnar.recipes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires a test db")
class RecipeRepositoryRecipeApplicationTests {

	@Value("${DB_HOST:NOT_FOUND}")
	String host;

	@Test
	void contextLoads() {
		System.out.println("host is " + host);
	}

}
