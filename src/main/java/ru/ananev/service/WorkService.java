package ru.ananev.service;

import ru.ananev.dto.WorkDTO;
import ru.ananev.entity.Car;
import ru.ananev.entity.Master;
import ru.ananev.entity.Service_;
import ru.ananev.entity.Work;
import ru.ananev.repository.CarRepository;
import ru.ananev.repository.MasterRepository;
import ru.ananev.repository.ServiceRepository;
import ru.ananev.repository.WorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkService {

    private final WorkRepository workRepository;
    private final MasterRepository masterRepository;
    private final CarRepository carRepository;
    private final ServiceRepository serviceRepository;

    public WorkService(WorkRepository workRepository,
                       MasterRepository masterRepository,
                       CarRepository carRepository,
                       ServiceRepository serviceRepository) {
        this.workRepository = workRepository;
        this.masterRepository = masterRepository;
        this.carRepository = carRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<WorkDTO> findAll() {
        // ИСПРАВЛЕННЫЙ ВЫЗОВ - используем правильное имя метода
        return workRepository.findAllByOrderByDateWorkDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ... остальные методы без изменений ...
    public WorkDTO findById(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Работа не найдена с id: " + id));
        return convertToDTO(work);
    }

    public WorkDTO create(WorkDTO workDTO) {
        // Проверяем ограничение: не более 2 работ в день на мастера
        if (!workRepository.canAssignWorkToMaster(workDTO.getMasterId(), workDTO.getDateWork())) {
            throw new RuntimeException("Мастер уже имеет максимальное количество работ на эту дату (2 работы)");
        }

        Work work = convertToEntity(workDTO);
        Work savedWork = workRepository.save(work);
        return convertToDTO(savedWork);
    }

    public WorkDTO update(Long id, WorkDTO workDTO) {
        Work existingWork = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Работа не найдена с id: " + id));

        // Если изменилась дата или мастер, проверяем ограничение
        if (!existingWork.getDateWork().equals(workDTO.getDateWork()) ||
                !existingWork.getMaster().getId().equals(workDTO.getMasterId())) {
            if (!workRepository.canAssignWorkToMaster(workDTO.getMasterId(), workDTO.getDateWork())) {
                throw new RuntimeException("Мастер уже имеет максимальное количество работ на эту дату (2 работы)");
            }
        }

        Master master = masterRepository.findById(workDTO.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));
        Car car = carRepository.findById(workDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
        Service_ service = serviceRepository.findById(workDTO.getServiceId())
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        existingWork.setDateWork(workDTO.getDateWork());
        existingWork.setMaster(master);
        existingWork.setCar(car);
        existingWork.setService(service);

        Work updatedWork = workRepository.save(existingWork);
        return convertToDTO(updatedWork);
    }

    public void delete(Long id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Работа не найдена с id: " + id));
        workRepository.delete(work);
    }

    public boolean canAssignWork(Long masterId, LocalDate date) {
        return workRepository.canAssignWorkToMaster(masterId, date);
    }

    private WorkDTO convertToDTO(Work work) {
        WorkDTO dto = new WorkDTO(
                work.getId(),
                work.getDateWork(),
                work.getMaster().getId(),
                work.getCar().getId(),
                work.getService().getId()
        );

        // Заполняем дополнительные поля для отображения
        dto.setMasterName(work.getMaster().getName());
        dto.setCarNumber(work.getCar().getNumber());
        dto.setCarMark(work.getCar().getMark());
        dto.setServiceName(work.getService().getName());
        dto.setActualCost(work.getActualCost().doubleValue());

        return dto;
    }

    private Work convertToEntity(WorkDTO workDTO) {
        Master master = masterRepository.findById(workDTO.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));
        Car car = carRepository.findById(workDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
        Service_ service = serviceRepository.findById(workDTO.getServiceId())
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        return new Work(workDTO.getDateWork(), master, car, service);
    }
}