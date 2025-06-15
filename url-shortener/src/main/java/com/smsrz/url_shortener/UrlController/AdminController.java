package com.smsrz.url_shortener.UrlController;


import com.smsrz.url_shortener.ApplicationProperties;
import com.smsrz.url_shortener.Model.PagedResult;
import com.smsrz.url_shortener.Model.ShortUrlDTO;
import com.smsrz.url_shortener.Service.ShortUrlService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ShortUrlService service;
    private final ApplicationProperties properties;

    public AdminController(ShortUrlService service, ApplicationProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    @GetMapping("/dashboard")
    public String adminUrls(@RequestParam(defaultValue = "1") int page, Model model){
        PagedResult<ShortUrlDTO> shortUrls = service.findAllShortUrls(page,properties.pageSize());
        model.addAttribute("shortUrls",shortUrls);
        model.addAttribute("baseUrl",properties.baseUrl());
        return "admin-dashboard";
    }

}
