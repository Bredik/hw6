package com.github.javarar.poke;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PokeServiceApplicationTests {
    @Autowired
    TestRestTemplate template;

    @Test
    void checkGetAll() {
        String expected = """
                [{"name":"pikachu","h":4.0,"w":60.0,"abilities":["static","lightning-rod"]}]
                """;
        ResponseEntity<String> real = template.getForEntity("/getAll?names=pikachu", String.class);

        Assertions.assertEquals(HttpStatus.OK, real.getStatusCode());
        Assertions.assertEquals(expected, real.getBody());
    }

    @Test
    void checkGetAllAsync() {
        String expected = """
                [{"name":"pikachu","h":4.0,"w":60.0,"abilities":["static","lightning-rod"]},
                {"name":"lapras","h":25.0,"w":2200.0,"abilities":["water-absorb","shell-armor","hydration"]}]
                """;
        ResponseEntity<String> real = template.getForEntity("/getAll?names=pikachu,lapras", String.class);

        Assertions.assertEquals(HttpStatus.OK, real.getStatusCode());
        Assertions.assertEquals(expected, real.getBody());
    }
}
