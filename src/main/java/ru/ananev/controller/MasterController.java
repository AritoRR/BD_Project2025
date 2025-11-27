package ru.ananev.controller;

import ru.ananev.dto.MasterDTO;
import ru.ananev.service.MasterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/masters")
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping
    public String listMasters(Model model) {
        List<MasterDTO> masters = masterService.findAll();
        Long mastersCount = masterService.getMastersCount();
        model.addAttribute("masters", masters);
        model.addAttribute("mastersCount", mastersCount);
        model.addAttribute("maxMasters", 10);
        return "masters/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("master", new MasterDTO());
        return "masters/create";
    }

    @PostMapping("/create")
    public String createMaster(@Valid @ModelAttribute("master") MasterDTO masterDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "masters/create";
        }

        try {
            masterService.create(masterDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Мастер успешно создан");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/masters/create";
        }

        return "redirect:/masters";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            MasterDTO masterDTO = masterService.findById(id);
            model.addAttribute("master", masterDTO);
            return "masters/edit";
        } catch (RuntimeException e) {
            return "redirect:/masters";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateMaster(@PathVariable Long id,
                               @Valid @ModelAttribute("master") MasterDTO masterDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "masters/edit";
        }

        try {
            masterService.update(id, masterDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Мастер успешно обновлен");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/masters/edit/" + id;
        }

        return "redirect:/masters";
    }

    @PostMapping("/delete/{id}")
    public String deleteMaster(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masterService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Мастер успешно удален");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/masters";
    }
}