package ru.ananev.controller;

import ru.ananev.dto.UserDTO;
import ru.ananev.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<UserDTO> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "users/create";
        }

        try {
            userService.create(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно создан");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/create";
        }

        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            UserDTO userDTO = userService.findById(id);
            model.addAttribute("user", userDTO);
            return "users/edit";
        } catch (RuntimeException e) {
            return "redirect:/users";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") UserDTO userDTO,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "users/edit";
        }

        try {
            userService.update(id, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно обновлен");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/edit/" + id;
        }

        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удален");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}