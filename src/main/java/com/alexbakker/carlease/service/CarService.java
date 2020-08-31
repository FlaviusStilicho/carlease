package com.alexbakker.carlease.service;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.repository.CarRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.InputMismatchException;

@Service
public class CarService {

    @Autowired
    CarRepository carRepository;

    /**
     * Validates if a new or edited Car object has its gross price set
     * @param car
     * @throws InputMismatchException
     */
    public void validateCar(Car car) throws InputMismatchException {
        if (car.getCarBrand() == null) {
            throw new InputMismatchException("Invalid car: Car brand cannot be empty");
        }
        if (car.getNetPrice() == null) {
            throw new InputMismatchException("Invalid car: Gross price cannot be empty");
        }
    }

    public void createCar(Car car){
        validateCar(car);
        car.setCurrentlyAssigned(false);

        carRepository.save(car);
    }

    public void updateCar(Car updatedCar, long id) throws NotFoundException {
        Car car = carRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Car with id %d not found", id)));

        validateCar(updatedCar);
        updatedCar.setId(car.getId());
        carRepository.save(updatedCar);
    }
}
