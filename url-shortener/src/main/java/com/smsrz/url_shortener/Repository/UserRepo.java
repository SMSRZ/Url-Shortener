package com.smsrz.url_shortener.Repository;

import com.smsrz.url_shortener.Model.Role;
import com.smsrz.url_shortener.UserEntity.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
@Repository
public class UserRepo /*extends JpaRepository<Users,Long>*/ {
    private final JdbcClient jdbcClient ;
    private static final Logger log = LoggerFactory.getLogger(UserRepo.class);

    public UserRepo(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Users> findByEmail(String email){
        String sql  = "select id,email,password,name,role,created_at from users where email = :email";
        return jdbcClient
                .sql(sql)
                .param("email",email)
                .query(new UserRowMapper())
                .optional();
    }

    public boolean existsByEmail(String email){
        String sql = "select count(*)>0 from users where email = :email";
        return jdbcClient
                .sql(sql)
                .param("email",email)
                .query(Boolean.class)
                .single();
    }

    public Optional<Users> findById(Long id){
        String sql = "SELECT id, email, password, name, role, created_at FROM users WHERE id = :id";
        return jdbcClient
                .sql(sql)
                .param("id",id)
                .query(new UserRowMapper())
                .optional();

    }

    public void save(Users user){
        String sql = """
                INSERT INTO users (email, password, name, role, created_at)
                VALUES (:email, :password, :name, :role, :createdAt)
                RETURNING id
                """;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql).param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("name", user.getName())
                .param("role", user.getRole().name())
                .param("createdAt", Timestamp.from(user.getCreatedAt()))
                .update(keyHolder);
        Long userId = keyHolder.getKeyAs(Long.class);
        log.info("User saved with id: {}", userId);
    }
    static class UserRowMapper implements RowMapper{

        @Override
        public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
            Users user = new Users();
            user.setId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setName(rs.getString("name"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return user;
        }
    }
}
