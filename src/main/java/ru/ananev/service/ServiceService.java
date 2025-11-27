package ru.ananev.service;

import ru.ananev.dto.ServiceDTO;
import ru.ananev.entity.Service_;
import ru.ananev.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDTO> findAll() {
        return serviceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ServiceDTO findById(Long id) {
        Service_ service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена с id: " + id));
        return convertToDTO(service);
    }

    public ServiceDTO create(ServiceDTO serviceDTO) {
        Service_ service = convertToEntity(serviceDTO);
        Service_ savedService = serviceRepository.save(service);
        return convertToDTO(savedService);
    }

    public ServiceDTO update(Long id, ServiceDTO serviceDTO) {
        Service_ existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена с id: " + id));

        existingService.setName(serviceDTO.getName());
        existingService.setCostOur(serviceDTO.getCostOur());
        existingService.setCostForeign(serviceDTO.getCostForeign());

        Service_ updatedService = serviceRepository.save(existingService);
        return convertToDTO(updatedService);
    }

    public void delete(Long id) {
        Service_ service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена с id: " + id));
        serviceRepository.delete(service);
    }

    public void increaseAllPrices(Double percentage) {
        List<Service_> services = serviceRepository.findAll();
        for (Service_ service : services) {
            BigDecimal multiplier = BigDecimal.ONE.add(BigDecimal.valueOf(percentage / 100));
            service.setCostOur(service.getCostOur().multiply(multiplier));
            service.setCostForeign(service.getCostForeign().multiply(multiplier));
        }
        serviceRepository.saveAll(services);
    }

    private ServiceDTO convertToDTO(Service_ service) {
        return new ServiceDTO(
                service.getId(),
                service.getName(),
                service.getCostOur(),
                service.getCostForeign()
        );
    }

    private Service_ convertToEntity(ServiceDTO serviceDTO) {
        return new Service_(
                serviceDTO.getName(),
                serviceDTO.getCostOur(),
                serviceDTO.getCostForeign()
        );
    }
}