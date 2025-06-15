package com.smsrz.url_shortener.UrlController;


import com.smsrz.url_shortener.Model.CreateUserCmd;
import com.smsrz.url_shortener.Model.Role;
import com.smsrz.url_shortener.Service.UserService;
import com.smsrz.url_shortener.UrlController.DTOs.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("user",new RegisterUserRequest("","",""));
                return "register";
    }

    @PostMapping("/register")
    String registerUser(@Valid @ModelAttribute("user") RegisterUserRequest registerUserRequest,
                      BindingResult result,
                      RedirectAttributes attributes){
        if (result.hasErrors()){
            return "register";
        }
        try {
            CreateUserCmd usercmd = new CreateUserCmd(
                    registerUserRequest.email(),
                    registerUserRequest.password(),
                    registerUserRequest.name(),
                    Role.ROLE_USER
            );
            service.createUser(usercmd);
            attributes.addFlashAttribute("successMessage","Registration Successful, please login");
            return "redirect:/login";
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage","Registration Failed"+e.getMessage());
        }
        return "redirect:/register";
    }
}
