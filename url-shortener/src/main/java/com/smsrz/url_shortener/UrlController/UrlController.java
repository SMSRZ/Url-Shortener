package com.smsrz.url_shortener.UrlController;


import com.smsrz.url_shortener.ApplicationProperties;
import com.smsrz.url_shortener.Exceptions.ShortUrlNotFoundException;
import com.smsrz.url_shortener.Model.CreateShortUrlCmd;
import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Service.ShortUrlService;
import com.smsrz.url_shortener.UrlController.DTOs.CreateShortUrlForm;
import com.smsrz.url_shortener.UserEntity.Users;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UrlController {


    private final ShortUrlService service;
    private final SecurityUtils utils;
    private final ApplicationProperties properties;

    public UrlController(ShortUrlService service, SecurityUtils utils, ApplicationProperties properties) {
        this.service = service;
        this.utils = utils;
        this.properties = properties;
    }

    @GetMapping("/home")
    public String home(Model model){
//        Users currentuser = utils.getCurrentUser();
        List<ShortUrlDTO> urls = service.findPublicShortUrls();
        model.addAttribute("shortUrls",urls);
        model.addAttribute("baseUrl",properties.baseurl());
        model.addAttribute("createShortUrlForm",new CreateShortUrlForm("",false,null));
        return "index";//thymeleaf doesnt include .extensions
    }
    @PostMapping("/short-urls")
    String createShortUrls(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                           BindingResult result,
                           RedirectAttributes attributes,
                           Model model){
        if (result.hasErrors()){
            List<ShortUrlDTO> urls = service.findPublicShortUrls();
            model.addAttribute("shortUrls",urls);
            model.addAttribute("baseUrl",properties.baseurl());
            return "index";
        }
        //TODO implement logic
        try {
            Long userId = utils.getCurrentUserId();
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(form.originalUrl(),form.isPrivate(),form.expirationInDays(),userId);
            var shortUrlDto = service.createShortUrl(cmd);
            attributes.addFlashAttribute("successMessage", properties.baseurl()+"s/"+shortUrlDto.shortKey());
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage","Unable to create Short url");
        }

        return "redirect:/home";
    }
    @GetMapping("/s/{shortKey}")
    public String redirectToOriginalUrl(@PathVariable String shortKey){
        long userId = utils.getCurrentUserId();
       Optional<ShortUrlDTO> shortUrlDTOOptional =  service.accessShortUrl(shortKey,userId);
       if(shortUrlDTOOptional.isEmpty()){
           throw new ShortUrlNotFoundException("Invalid Short Key"+shortKey);
       }
       ShortUrlDTO shortUrlDTO = shortUrlDTOOptional.get();
       return "redirect:"+shortUrlDTO.originalUrl();
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
