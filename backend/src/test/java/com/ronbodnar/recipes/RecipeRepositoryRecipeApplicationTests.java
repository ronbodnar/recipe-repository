package com.ronbodnar.recipes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires a test db")
class RecipeRepositoryRecipeApplicationTests {

	@Test
	void contextLoads() {
	}

}
