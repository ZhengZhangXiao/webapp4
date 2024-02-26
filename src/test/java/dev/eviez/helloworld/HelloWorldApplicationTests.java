package dev.eviez.helloworld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class HelloWorldApplicationTests {
//    @Autowired
//    private MockMvc mockMvc;
//    @Test
//    public void testCreateAccount() throws Exception {
//        String userJson = "{\"id\":\"100\",\"email\":\"test@gmail.com\", \"password\":\"1234567\",\"firstName\":\"test\",\"lastName\":\"test\"}";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userJson))
//                .andExpect(MockMvcResultMatchers.status().isOk()) // or any other expected status code
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@gmail.com")); // assert based on expected response
//    }
//
//    @Test
//    public void testUpdateAccount() throws Exception {
//        String updatedUserJson =" {\"id\":\"100\",\"email\":\"test@gmail.com\", \"password\":\"1234567\",\"firstName\":\"test01\",\"lastName\":\"test02\"}";
//
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/v1/user")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(updatedUserJson))
//                .andExpect(MockMvcResultMatchers.status().isOk()) // or any other expected status code
//                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("newpass")); // assert based on expected response
//    }


    @LocalServerPort
    private int port;

    @Autowired
    private  TestRestTemplate restTemplate;

    private String getRootUrl() {
        return "http://localhost:" + port + "/users";
    }

    @BeforeEach
    public  void waitForApplicationUp() {
        Awaitility.await()
                .atMost(Duration.ofMinutes(2))
                .pollInterval(Duration.ofSeconds(10))
                .until(this::isAppUp);
    }
    private  boolean isAppUp() {
        String url = getRootUrl();
        try {
            restTemplate.getForEntity(url, String.class);
            return true;
        } catch (HttpClientErrorException ex) {
            return false;
        }
    }
    @Test
    public void testCreateUser() {
        String userJson = "{\"id\":\"100\",\"email\":\"test20@gmail.com\", \"password\":\"1234567\",\"firstName\":\"test06\",\"lastName\":\"test06\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(userJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(getRootUrl(), entity, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Additional assertions to validate the response body
        // Note: Since the response is a String, you might need to parse it to JSON for detailed assertions
    }

    @Test
    public void testUpdateUser() {
        TestRestTemplate authRestTemplate = restTemplate.withBasicAuth("test03@gmail.com", "1234567");

        // Step 1: Create a user
        String createUserJson = "{\"id\":\"105\",\"email\":\"test21@gmail.com\", \"password\":\"1234567\",\"firstName\":\"test01\",\"lastName\":\"test01\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> createEntity = new HttpEntity<>(createUserJson, headers);
        ResponseEntity<String> createResponse = authRestTemplate.postForEntity(getRootUrl(), createEntity, String.class);
        assertNotNull(createResponse);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Long userId = extractUserIdFromResponse(createResponse);

        // Step 2: Update the user
        String updateUserJson = "{ \"firstName\":\"test21\",\"lastName\":\"test21\"}";
        HttpEntity<String> updateEntity = new HttpEntity<>(updateUserJson, headers);
        // Assuming /{id} is the endpoint for updating user. Replace "{id}" with actual user ID.
        authRestTemplate.put(getRootUrl() + "/"+userId, updateEntity);

        // Step 3: Retrieve the updated user
        // Assuming /{id} is the endpoint for retrieving user. Replace "{id}" with actual user ID.
        ResponseEntity<String> getResponse = authRestTemplate.getForEntity(getRootUrl() +"/" + userId, String.class);
        assertNotNull(getResponse);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        // Step 4: Assert the retrieved information matches the updated information
        // You will need to parse the getResponse.getBody() to verify updated details
    }
    private Long extractUserIdFromResponse(ResponseEntity<String> response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());

            // Assuming the user ID is in a field named 'id' in the JSON response
            if (root.has("id") && root.get("id").canConvertToLong()) {
                return root.get("id").asLong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception if the ID is mandatory
    }

}

