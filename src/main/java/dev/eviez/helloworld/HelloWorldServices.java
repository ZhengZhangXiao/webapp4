package dev.eviez.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class HelloWorldServices {


    @Autowired
    private HelloWorldRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public HelloWorldEntity createUser(HelloWorldEntity user) {
       user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
       user.setAccountCreated(LocalDateTime.now());
       user.setAccountUpdated(LocalDateTime.now());
        return userRepository.save(user);
    }

   public Optional<HelloWorldEntity> getUserById(Long id){
          return userRepository.findById(id);
   }

   public HelloWorldEntity updateUser(Long id, HelloWorldEntity updatedUser){
       return userRepository.findById(id).map(user -> {
           if (updatedUser.getFirstName() != null) {
               user.setFirstName(updatedUser.getFirstName());
           }
           if (updatedUser.getLastName() != null) {
               user.setLastName(updatedUser.getLastName());
           }
           if (updatedUser.getPassword() != null) {
               user.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
           }
           user.setAccountUpdated(LocalDateTime.now());
           return userRepository.save(user);

       }).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
   }

   }


