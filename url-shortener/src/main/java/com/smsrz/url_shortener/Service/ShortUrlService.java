package com.smsrz.url_shortener.Service;


import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Repository.ShortUrlRepo;
import com.smsrz.url_shortener.UserEntity.ShortUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortUrlService {


    private final ShortUrlRepo repo;

    private final EntityMapper entityMapper;

    public ShortUrlService(ShortUrlRepo repo, EntityMapper entityMapper) {
        this.repo = repo;
        this.entityMapper = entityMapper;
    }

    public List<ShortUrlDTO> findPublicShortUrls() {
        return repo.findPublicShortUrls().stream().map(entityMapper::toShortUrlDTO).toList();
    }
}
