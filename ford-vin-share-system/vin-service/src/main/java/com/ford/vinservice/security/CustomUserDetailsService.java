package com.ford.vinservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final ConcurrentHashMap<String, CustomUserDetails> users = new ConcurrentHashMap<>();

    public CustomUserDetailsService() {

        users.put("admin", new CustomUserDetails("admin",
            "$2a$10$N5XKjYcX5ZqJ5Z5Z5Z5Z5.5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5",
            "ADMIN", null));

        users.put("customer1", new CustomUserDetails("customer1",
            "$2a$10$N5XKjYcX5ZqJ5Z5Z5Z5Z5.5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5",
            "CUSTOMER", 1L));

        users.put("dealer1", new CustomUserDetails("dealer1",
            "$2a$10$N5XKjYcX5ZqJ5Z5Z5Z5Z5.5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5",
            "DEALER", 100L));
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }

    public void addUser(CustomUserDetails user) {
        users.put(user.getUsername(), user);
    }

    public CustomUserDetails getUserByUsername(String username) {
        return users.get(username);
    }
}
