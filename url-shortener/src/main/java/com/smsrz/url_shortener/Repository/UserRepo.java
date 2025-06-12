package com.smsrz.url_shortener.Repository;

import com.smsrz.url_shortener.UserEntity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users,Long> {
    Optional<Users> findByEmail(String email);
}
