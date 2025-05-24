package com.affiliate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.affiliate.model.User;
import com.affiliate.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

 @Autowired
 private UserRepository userRepository;

 @Override
 public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
     Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);
     if (!userOptional.isPresent()) {
         userOptional = userRepository.findByEmail(usernameOrEmail);
     }

     User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

     return new org.springframework.security.core.userdetails.User(
             user.getUsername(),
             user.getPassword(),
             Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toUpperCase()))
     );
 }
}