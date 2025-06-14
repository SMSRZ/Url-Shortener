package com.smsrz.url_shortener.Service;


import com.smsrz.url_shortener.ApplicationProperties;
import com.smsrz.url_shortener.Model.CreateShortUrlCmd;
import com.smsrz.url_shortener.Model.PagedResult;
import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Repository.ShortUrlRepo;
import com.smsrz.url_shortener.UserEntity.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    public PagedResult<ShortUrlDTO> findPublicShortUrls(int pageNo,int pageSize) {
        pageNo=pageNo>1 ? pageNo-1 : 0;
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<ShortUrlDTO> shortUrlDTOPage = repo.findPublicShortUrls(pageable).map(entityMapper::toShortUrlDTO);
        return PagedResult.from(shortUrlDTOPage);
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
        if (cmd.userId()==null){
            url.setCreatedBy(null);
            url.setIsPrivate(false);
            url.setExpiresAt(Instant.now().plus(properties.defaultExpiryDays(), ChronoUnit.DAYS));
        }else {
            url.setCreatedBy(repo.findById(cmd.userId()).orElseThrow().getCreatedBy());
            url.setIsPrivate(cmd.isPrivate() != null && cmd.isPrivate());
            url.setExpiresAt(cmd.expirationInDays()!=null?Instant.now().plus(cmd.expirationInDays(), ChronoUnit.DAYS):null);
        }
        url.setClickCount(1L);
        url.setCreatedAt(Instant.now());

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
    @Transactional
    public Optional<ShortUrlDTO> accessShortUrl(String shortKey,Long userId) {
        Optional<ShortUrl> shortUrlOptional = repo.findByShortKey(shortKey);
        if(shortUrlOptional.isEmpty()){
            return Optional.empty();
        }
        ShortUrl shortUrl = shortUrlOptional.get();
        if (shortUrl.getExpiresAt()!=null&&shortUrl.getExpiresAt().isBefore(Instant.now())){
            return Optional.empty();
        }
        if (shortUrl.getIsPrivate()!=null && shortUrl.getCreatedBy()!=null && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)){
            return Optional.empty();
        }
        shortUrl.setClickCount(shortUrl.getClickCount()+1);
        repo.save(shortUrl);
        return shortUrlOptional.map(entityMapper::toShortUrlDTO);
    }
}
