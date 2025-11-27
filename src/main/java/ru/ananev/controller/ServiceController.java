package ru.ananev.controller;

import ru.ananev.dto.ServiceDTO;
import ru.ananev.service.ServiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public String listServices(Model model) {
        List<ServiceDTO> services = serviceService.findAll();
        model.addAttribute("services", services);
        return "services/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("service", new ServiceDTO());
        return "services/create";
    }

    @PostMapping("/create")
    public String createService(@Valid @ModelAttribute("service") ServiceDTO serviceDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "services/create";
        }

        try {
            serviceService.create(serviceDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Услуга успешно создана");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/services/create";
        }

        return "redirect:/services";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            ServiceDTO serviceDTO = serviceService.findById(id);
            model.addAttribute("service", serviceDTO);
            return "services/edit";
        } catch (RuntimeException e) {
            return "redirect:/services";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable Long id,
                                @Valid @ModelAttribute("service") ServiceDTO serviceDTO,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "services/edit";
        }

        try {
            serviceService.update(id, serviceDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Услуга успешно обновлена");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/services/edit/" + id;
        }

        return "redirect:/services";
    }

    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Услуга успешно удалена");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/services";
    }

    @PostMapping("/increase-prices")
    public String increasePrices(@RequestParam Double percentage, RedirectAttributes redirectAttributes) {
        try {
            serviceService.increaseAllPrices(percentage);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Цены успешно увеличены на " + percentage + "%");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/services";
    }
}