package com.smsrz.url_shortener.UrlController;


import com.smsrz.url_shortener.ApplicationProperties;
import com.smsrz.url_shortener.Exceptions.ShortUrlNotFoundException;
import com.smsrz.url_shortener.Model.CreateShortUrlCmd;
import com.smsrz.url_shortener.Model.PagedResult;
import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Service.ShortUrlService;
import com.smsrz.url_shortener.UrlController.DTOs.CreateShortUrlForm;
import com.smsrz.url_shortener.UserEntity.Users;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
    private void addShortUrlDataToModel(Model model,int pageNo){
        PagedResult<ShortUrlDTO> urls = service.findPublicShortUrls(pageNo, properties.pagezSize());
        model.addAttribute("shortUrls",urls);
        model.addAttribute("baseUrl",properties.baseurl());
    }
    //if the request contains GET/home?page=0&size=10&sort="createdAt",Desc  then spring data jpa will automatically convert the fields to a pageable object
    //but this is diectly coupling the web layer with persistence provider spiringdatajpa in this case so what would happen if i change the jpa provider
    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "1") int page,
//            Pageable pageable,
            Model model){

//        Users currentuser = utils.getCurrentUser();
        this.addShortUrlDataToModel(model,page);
        model.addAttribute("createShortUrlForm",new CreateShortUrlForm("",false,null));
        return "index";//thymeleaf doesnt include .extensions
    }
    @PostMapping("/short-urls")
    String createShortUrls(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                           BindingResult result,
                           RedirectAttributes attributes,
                           Model model){
        if (result.hasErrors()){
            this.addShortUrlDataToModel(model,1);
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
