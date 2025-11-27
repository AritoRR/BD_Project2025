package ru.ananev.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Проверяем роль пользователя
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверный логин или пароль!");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы!");
        }
        return "login";
    }
}