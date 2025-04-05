package com.learncode.schoolDev.service;

import com.learncode.schoolDev.model.User;
import com.learncode.schoolDev.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà.");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    user.setPasswordHash(updatedUser.getPasswordHash());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec ID : " + id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec ID : " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDetails loadUserByUsername(String mail) {
       User user = userRepository.findByEmail(mail)
               .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec le nom d'utilisateur : " + mail));
        if(user == null) {
            throw new UsernameNotFoundException("user not found with username: " + mail);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())
                )
        );
    }
}