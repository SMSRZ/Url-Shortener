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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
        PagedResult<ShortUrlDTO> urls = service.findPublicShortUrls(pageNo, properties.pageSize());
        model.addAttribute("shortUrls",urls);
        model.addAttribute("baseUrl",properties.baseUrl());
    }
    //if the request contains GET/home?page=0&size=10&sort="createdAt",Desc  then spring data jpa will automatically convert the fields to a pageable object
    //but this is diectly coupling the web layer with persistence provider springdatajpa in this case so what would happen if i change the jpa provider
    @GetMapping("/home")
    public String home(
            @RequestParam(defaultValue = "1") Integer page,
            Model model) {
        this.addShortUrlDataToModel(model, page);
        model.addAttribute("paginationUrl","/home");
        model.addAttribute("createShortUrlForm",
                new CreateShortUrlForm("", false, null));
        return "index";
    }
    @PostMapping("/short-urls")
    String createShortUrls(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                           BindingResult bindingResult,
                           RedirectAttributes attributes,
                           Model model){
        if(bindingResult.hasErrors()) {
            this.addShortUrlDataToModel(model, 1);
            return "index";
        }

        try {
            Long userId = utils.getCurrentUserId();
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(
                    form.originalUrl(),
                    form.isPrivate(),
                    form.expirationInDays(),
                    userId
            );
            var shortUrlDto = service.createShortUrl(cmd);
            attributes.addFlashAttribute("successMessage", "Short URL created successfully "+
                    properties.baseUrl()+"/s/"+shortUrlDto.shortKey());
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Failed to create short URL");

        }
        return "redirect:/home";
    }
    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey) {
        Long userId = utils.getCurrentUserId();
        Optional<ShortUrlDTO> shortUrlDtoOptional = service.accessShortUrl(shortKey, userId);
        if(shortUrlDtoOptional.isEmpty()) {
            throw new ShortUrlNotFoundException("Invalid short key: "+shortKey);
        }
        ShortUrlDTO shortUrlDto = shortUrlDtoOptional.get();
        return "redirect:"+shortUrlDto.originalUrl();
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("my-urls")
    public String myurls(@RequestParam(defaultValue = "1")int page,Model model){
        Long userId = utils.getCurrentUserId();
        PagedResult<ShortUrlDTO> myurls = service.findUserShortUrls(page, userId, properties.pageSize());
        model.addAttribute("paginationUrl","/my-urls");
        model.addAttribute("shortUrls",myurls);
        model.addAttribute("baseUrl",properties.baseUrl());
        return "my-urls";
    }

    @PostMapping("/delete-urls")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String deleteUrls(@RequestParam(value = "ids")List<Long> ids ,RedirectAttributes attributes){
        if (ids==null||ids.isEmpty()){
            attributes.addFlashAttribute("errorMessage","No Urls to delete");
            return "redirect:/my-urls";
        }
        try{
            Long userId = utils.getCurrentUserId();
            service.deleteUserId(ids,userId);
            attributes.addFlashAttribute("successMessage","The selected Urls have been deleted Successfully");

        }catch (Exception e){
            attributes.addFlashAttribute("errorMessage",
                    "Error Occured in deleting some urls"+e.getMessage());
        }
        return "redirect:/my-urls";
    }
}
