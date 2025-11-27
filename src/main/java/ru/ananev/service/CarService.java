package ru.ananev.service;

import ru.ananev.dto.CarDTO;
import ru.ananev.entity.Car;
import ru.ananev.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarDTO> findAll() {
        return carRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CarDTO findById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с id: " + id));
        return convertToDTO(car);
    }

    public CarDTO create(CarDTO carDTO) {
        // Проверка на уникальность номера
        if (carRepository.existsByNumber(carDTO.getNumber())) {
            throw new RuntimeException("Автомобиль с номером " + carDTO.getNumber() + " уже существует");
        }

        Car car = convertToEntity(carDTO);
        Car savedCar = carRepository.save(car);
        return convertToDTO(savedCar);
    }

    public CarDTO update(Long id, CarDTO carDTO) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с id: " + id));

        // Проверка на уникальность номера (если номер изменился)
        if (!existingCar.getNumber().equals(carDTO.getNumber()) &&
                carRepository.existsByNumber(carDTO.getNumber())) {
            throw new RuntimeException("Автомобиль с номером " + carDTO.getNumber() + " уже существует");
        }

        existingCar.setNumber(carDTO.getNumber());
        existingCar.setColor(carDTO.getColor());
        existingCar.setMark(carDTO.getMark());
        existingCar.setIsForeign(carDTO.getIsForeign());

        Car updatedCar = carRepository.save(existingCar);
        return convertToDTO(updatedCar);
    }

    public void delete(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден с id: " + id));
        carRepository.delete(car);
    }

    public boolean existsByNumber(String number) {
        return carRepository.existsByNumber(number);
    }

    private CarDTO convertToDTO(Car car) {
        return new CarDTO(
                car.getId(),
                car.getNumber(),
                car.getColor(),
                car.getMark(),
                car.getIsForeign()
        );
    }

    private Car convertToEntity(CarDTO carDTO) {
        return new Car(
                carDTO.getNumber(),
                carDTO.getColor(),
                carDTO.getMark(),
                carDTO.getIsForeign()
        );
    }
}