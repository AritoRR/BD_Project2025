package ru.ananev.service;

import ru.ananev.dto.MasterDTO;
import ru.ananev.entity.Master;
import ru.ananev.repository.MasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MasterService {

    private final MasterRepository masterRepository;

    public MasterService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public List<MasterDTO> findAll() {
        return masterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MasterDTO findById(Long id) {
        Master master = masterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Мастер не найден с id: " + id));
        return convertToDTO(master);
    }

    public MasterDTO create(MasterDTO masterDTO) {
        // Проверка ограничения на количество мастеров
        if (!masterRepository.canAddNewMaster()) {
            throw new RuntimeException("Невозможно добавить мастера. Превышен лимит в 10 мастеров");
        }

        Master master = convertToEntity(masterDTO);
        Master savedMaster = masterRepository.save(master);
        return convertToDTO(savedMaster);
    }

    public MasterDTO update(Long id, MasterDTO masterDTO) {
        Master existingMaster = masterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Мастер не найден с id: " + id));

        existingMaster.setName(masterDTO.getName());
        Master updatedMaster = masterRepository.save(existingMaster);
        return convertToDTO(updatedMaster);
    }

    public void delete(Long id) {
        Master master = masterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Мастер не найден с id: " + id));
        masterRepository.delete(master);
    }

    public Long getMastersCount() {
        return masterRepository.countAllMasters();
    }

    private MasterDTO convertToDTO(Master master) {
        return new MasterDTO(master.getId(), master.getName());
    }

    private Master convertToEntity(MasterDTO masterDTO) {
        return new Master(masterDTO.getName());
    }
}