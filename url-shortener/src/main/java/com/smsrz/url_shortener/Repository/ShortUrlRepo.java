package com.smsrz.url_shortener.Repository;

import com.smsrz.url_shortener.UserEntity.ShortUrl;
import com.smsrz.url_shortener.UserEntity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlRepo extends JpaRepository<ShortUrl,Long> {
    @Query("select su from ShortUrl su left join fetch su.createdBy where su.isPrivate = false")
    Page<ShortUrl> findPublicShortUrls(Pageable pageable);

    Page<ShortUrl> findByCreatedById(Long userid,Pageable page);

    boolean existsByShortKey(String shortKey);

    Optional<ShortUrl> findByShortKey(String shortKey);

    @Modifying
    @Query("DELETE FROM ShortUrl s WHERE s.id IN :ids AND s.createdBy = :user")
    void deleteByIdInAndCreatedBy(List<Long> ids, Users user);

    @Query("select u from ShortUrl u left join fetch u.createdBy")
    Page<ShortUrl> findAllShortUrls(Pageable pageable);
}

//    @Query("select su from ShortUrl su where su.isPrivate = false order by su.createdAt desc")
//    @EntityGraph(attributePaths = "createdBy")
    //without using left join or entity graph these query result in a n+1 problem where a lot of database queries are fired
    //slowing down the application aka lazy loading instead eager loading is preferred making the application faster
//    List<ShortUrl> findByIsPrivateIsFalseOrderByCreatedAtDesc();