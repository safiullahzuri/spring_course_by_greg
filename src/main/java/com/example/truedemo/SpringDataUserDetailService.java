package com.example.truedemo;

import com.example.truedemo.models.User;
import com.example.truedemo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringDataUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public SpringDataUserDetailService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Stream.of(user.getRoles()).map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

    }
}
