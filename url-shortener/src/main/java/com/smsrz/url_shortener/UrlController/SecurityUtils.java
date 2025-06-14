package com.smsrz.url_shortener.UrlController;

import com.smsrz.url_shortener.Repository.UserRepo;
import com.smsrz.url_shortener.UserEntity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {
    private final UserRepo repo;

    public SecurityUtils(UserRepo repo) {
        this.repo = repo;
    }

    public Users getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication!=null && authentication.isAuthenticated()){
            String email = authentication.getName();
            return repo.findByEmail(email).orElse(null);
        }
        return null;
    }
    public Long getCurrentUserId(){
        Users user = getCurrentUser();
        return (user!=null) ? user.getId() : null;
    }
}
