package com.smsrz.url_shortener.UrlController;


import com.smsrz.url_shortener.ApplicationProperties;
import com.smsrz.url_shortener.Model.CreateShortUrlCmd;
import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Service.ShortUrlService;
import com.smsrz.url_shortener.UrlController.DTOs.CreateShortUrlForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UrlController {

    @Autowired
    private ShortUrlService service;

    @Autowired
    private ApplicationProperties properties;

    @GetMapping("/home")
    public String home(Model model){
        List<ShortUrlDTO> urls = service.findPublicShortUrls();
        model.addAttribute("shortUrls",urls);
        model.addAttribute("baseUrl",properties.baseurl());
        model.addAttribute("createShortUrlForm",new CreateShortUrlForm(""));
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
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(form.originalUrl());
            var shortUrlDto = service.createShortUrl(cmd);
            attributes.addFlashAttribute("successMessage", properties.baseurl()+shortUrlDto.shortKey());
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage","Unable to create Short url");
        }

        return "redirect:/home";
    }
}
