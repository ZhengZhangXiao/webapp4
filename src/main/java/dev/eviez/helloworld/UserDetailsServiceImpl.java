package dev.eviez.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private HelloWorldRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        HelloWorldEntity user = new HelloWorldEntity();
        List<HelloWorldEntity> users = userRepository.findAll();
        boolean userFound = false;
        for(HelloWorldEntity curuser: users) {
            if(curuser.getEmail().equals(username)) {
                user = curuser;
                userFound = true;
                break;
            }
        }
        if(!userFound) {
            System.out.println("User not found with email: " + username);
        }
        else {
            System.out.println("User found with email: " + username);
        }

//        HelloWorldEntity user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER") // Specify the user's roles
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}