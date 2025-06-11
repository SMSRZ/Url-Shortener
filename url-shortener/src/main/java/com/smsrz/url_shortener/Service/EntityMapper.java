package com.smsrz.url_shortener.Service;


import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Model.UserDTO;
import com.smsrz.url_shortener.UserEntity.ShortUrl;
import com.smsrz.url_shortener.UserEntity.Users;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {
    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl){
        UserDTO userDTO = null;
        if (shortUrl.getCreatedBy()!=null){
            userDTO = toUserDTO(shortUrl.getCreatedBy());
        }
        return new ShortUrlDTO(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                userDTO,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

    private UserDTO toUserDTO(Users user) {
        return new UserDTO(user.getId(), user.getName());
    }



}
