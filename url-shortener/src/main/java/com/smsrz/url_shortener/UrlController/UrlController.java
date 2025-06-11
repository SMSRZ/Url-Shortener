package com.smsrz.url_shortener.UrlController;


import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UrlController {

    @Autowired
    private ShortUrlService service;

    @GetMapping("/home")
    public String home(Model model){
        List<ShortUrlDTO> urls = service.findPublicShortUrls();
        model.addAttribute("shortUrls",urls);
        model.addAttribute("baseUrl","http://localhost:8080");
        return "index";//thymeleaf doesnt include .extensions
    }
}
