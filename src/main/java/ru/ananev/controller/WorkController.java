package ru.ananev.controller;

import ru.ananev.dto.WorkDTO;
import ru.ananev.entity.Car;
import ru.ananev.entity.Master;
import ru.ananev.entity.Service_;
import ru.ananev.repository.CarRepository;
import ru.ananev.repository.MasterRepository;
import ru.ananev.repository.ServiceRepository;
import ru.ananev.service.WorkService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/works")
public class WorkController {

    private final WorkService workService;
    private final MasterRepository masterRepository;
    private final CarRepository carRepository;
    private final ServiceRepository serviceRepository;

    public WorkController(WorkService workService,
                          MasterRepository masterRepository,
                          CarRepository carRepository,
                          ServiceRepository serviceRepository) {
        this.workService = workService;
        this.masterRepository = masterRepository;
        this.carRepository = carRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public String listWorks(Model model) {
        List<WorkDTO> works = workService.findAll();
        model.addAttribute("works", works);
        return "works/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        WorkDTO workDTO = new WorkDTO();
        workDTO.setDateWork(LocalDate.now()); // Устанавливаем текущую дату по умолчанию

        model.addAttribute("work", workDTO);
        addDropdownDataToModel(model);
        return "works/create";
    }

    @PostMapping("/create")
    public String createWork(@Valid @ModelAttribute("work") WorkDTO workDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addDropdownDataToModel(model);
            return "works/create";
        }

        try {
            workService.create(workDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Работа успешно назначена");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            addDropdownDataToModel(model);
            return "works/create";
        }

        return "redirect:/works";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            WorkDTO workDTO = workService.findById(id);
            model.addAttribute("work", workDTO);
            addDropdownDataToModel(model);
            return "works/edit";
        } catch (RuntimeException e) {
            return "redirect:/works";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateWork(@PathVariable Long id,
                             @Valid @ModelAttribute("work") WorkDTO workDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addDropdownDataToModel(model);
            return "works/edit";
        }

        try {
            workService.update(id, workDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Работа успешно обновлена");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            addDropdownDataToModel(model);
            return "redirect:/works/edit/" + id;
        }

        return "redirect:/works";
    }

    @PostMapping("/delete/{id}")
    public String deleteWork(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            workService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Работа успешно удалена");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/works";
    }

    // Вспомогательный метод для добавления данных в выпадающие списки
    private void addDropdownDataToModel(Model model) {
        List<Master> masters = masterRepository.findAll();
        List<Car> cars = carRepository.findAll();
        List<Service_> services = serviceRepository.findAll();

        model.addAttribute("masters", masters);
        model.addAttribute("cars", cars);
        model.addAttribute("services", services);
    }
}