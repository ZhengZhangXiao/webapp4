package dev.eviez.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class HelloWorldController {
    @Autowired
    private HelloWorldRepository userRepository;
    @Autowired
    private HelloWorldServices userService;
    @Autowired
    private HealthEndpoint healthEndpoint;
    @GetMapping("/")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/bye")
    public String bye() {
        return "bye bye!";
    }


    @GetMapping("/healthz")
    public ResponseEntity<Void> healthz(HttpEntity<String> httpEntity) {
        // Check if the request has any payload
        if (httpEntity.getBody() != null && !httpEntity.getBody().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        HealthComponent health = healthEndpoint.health();
        if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache")
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Cache-Control", "no-cache")
                    .build();
        }
    }

    @PostMapping("/healthz")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void healthzPost() {

    }


    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody HelloWorldEntity user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        HelloWorldEntity createdUser = userService.createUser(user);
        // Exclude password from response
        createdUser.setPassword(null);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody HelloWorldEntity user) {
        try {
            HelloWorldEntity updatedUser = userService.updateUser(id, user);
            updatedUser.setPassword(null); // Ensure the password is not sent back in the response
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        // Additional catch blocks for other exceptions if needed
    }

    @GetMapping("users/{id}")
    public ResponseEntity<HelloWorldEntity> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    user.setPassword(null); // Ensure the password is not sent back in the response
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
