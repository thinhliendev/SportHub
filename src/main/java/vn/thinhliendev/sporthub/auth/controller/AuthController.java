package vn.thinhliendev.sporthub.auth.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.thinhliendev.sporthub.auth.dto.RegistrationForm;
import vn.thinhliendev.sporthub.auth.service.EmailAlreadyExistsException;
import vn.thinhliendev.sporthub.auth.service.UserService;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegistrationForm registrationForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (!registrationForm.getPassword().equals(registrationForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Mật khẩu xác nhận không khớp");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(registrationForm);
        } catch (EmailAlreadyExistsException exception) {
            bindingResult.rejectValue("email", "email.exists", exception.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("registrationSuccess", "Đăng ký thành công. Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}
