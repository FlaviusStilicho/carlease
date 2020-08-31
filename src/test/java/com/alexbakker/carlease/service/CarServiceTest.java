package com.alexbakker.carlease.service;

import com.alexbakker.carlease.model.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

class CarServiceTest {

    private CarService victim = new CarService();

    @Test
    void testValidateCar(){
        Car car = Car.builder().netPrice(BigDecimal.valueOf(30000L)).build();
        victim.validateCar(car);
    }

    @Test
    void testValidateCarNoNetValue(){
        Car car = new Car();

        Assertions.assertThrows(InputMismatchException.class, () -> {
            victim.validateCar(car);
        });
    }
}