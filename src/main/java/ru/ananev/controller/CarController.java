package ru.ananev.controller;

import ru.ananev.dto.CarDTO;
import ru.ananev.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(Model model) {
        List<CarDTO> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "cars/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("car", new CarDTO());
        return "cars/create";
    }

    @PostMapping("/create")
    public String createCar(@Valid @ModelAttribute("car") CarDTO carDTO,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cars/create";
        }

        try {
            carService.create(carDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Автомобиль успешно создан");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cars/create";
        }

        return "redirect:/cars";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            CarDTO carDTO = carService.findById(id);
            model.addAttribute("car", carDTO);
            return "cars/edit";
        } catch (RuntimeException e) {
            return "redirect:/cars";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute("car") CarDTO carDTO,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cars/edit";
        }

        try {
            carService.update(id, carDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Автомобиль успешно обновлен");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cars/edit/" + id;
        }

        return "redirect:/cars";
    }

    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            carService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Автомобиль успешно удален");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cars";
    }
}