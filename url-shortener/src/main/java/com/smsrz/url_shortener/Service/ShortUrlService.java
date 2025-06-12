package com.smsrz.url_shortener.Service;


import com.smsrz.url_shortener.ApplicationProperties;
import com.smsrz.url_shortener.Model.CreateShortUrlCmd;
import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Repository.ShortUrlRepo;
import com.smsrz.url_shortener.UserEntity.ShortUrl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {


    private final ShortUrlRepo repo;

    private final EntityMapper entityMapper;

    private final ApplicationProperties properties;

    public ShortUrlService(ShortUrlRepo repo, EntityMapper entityMapper, ApplicationProperties properties) {
        this.repo = repo;
        this.entityMapper = entityMapper;
        this.properties = properties;
    }

    public List<ShortUrlDTO> findPublicShortUrls() {
        return repo.findPublicShortUrls().stream().map(entityMapper::toShortUrlDTO).toList();
    }
@Transactional
    public ShortUrlDTO createShortUrl(CreateShortUrlCmd cmd){
        if (properties.validateOriginalUrl()){
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            if(!urlExists){
                throw new RuntimeException("Invalid Url");
            }
        }
        var shortkey = generateRandomShortKey();
        var url =  new ShortUrl();
        url.setOriginalUrl(cmd.originalUrl());
        url.setShortKey(shortkey);
        url.setCreatedBy(null);
        url.setClickCount(1L);
        url.setCreatedAt(Instant.now());
        url.setIsPrivate(false);
        url.setExpiresAt(Instant.now().plus(properties.defaultExpiryDays(), ChronoUnit.DAYS));
        repo.save(url);
        return entityMapper.toShortUrlDTO(url);
    }
    public String generateUniqueShortkey(){
        String shortKey;
        do {
            shortKey=generateRandomShortKey();
        }while (repo.existsByShortKey(shortKey));
        return shortKey;
    }

    private static final String CHARACTERS ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH =6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomShortKey(){
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for(int i=0;i<SHORT_KEY_LENGTH;i++){
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
