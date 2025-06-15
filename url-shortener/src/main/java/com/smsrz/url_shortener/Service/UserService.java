package com.smsrz.url_shortener.Service;

import com.smsrz.url_shortener.Model.CreateUserCmd;
import com.smsrz.url_shortener.Repository.UserRepo;
import com.smsrz.url_shortener.UserEntity.Users;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smsrz.url_shortener.Config.WebSecurityConfig;

import java.time.Instant;
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepo repo,PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder=passwordEncoder;
    }
    @Transactional
    public void createUser(CreateUserCmd usercmd) {
        if (repo.existsByEmail(usercmd.email())){
            throw new RuntimeException("Email already Exists");
        }
        Users user = new Users();
        user.setEmail(usercmd.email());
        user.setPassword(passwordEncoder.encode(usercmd.password()));
        user.setRole(usercmd.role());
        user.setName(usercmd.name());
        user.setCreatedAt(Instant.now());
        repo.save(user);
    }
}
