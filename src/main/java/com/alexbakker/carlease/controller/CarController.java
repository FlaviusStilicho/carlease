package com.alexbakker.carlease.controller;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import com.alexbakker.carlease.repository.CarHistoryRepository;
import com.alexbakker.carlease.repository.CarRepository;
import com.alexbakker.carlease.repository.CustomerRepository;
import com.alexbakker.carlease.service.CarService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.InputMismatchException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    CarRepository carRepository;
    @Autowired
    CarHistoryRepository carHistoryRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CarService carService;

    /**
     * Lists all cars
     *
     * @return List<Car>
     */
    @GetMapping()
    @PreAuthorize("hasRole('LEASE') or hasRole('BROKER')")
    public List<Car> findAll() {
        log.info("Listing all cars");
        return carRepository.findAll();
    }

    /**
     * Finds a car by ID
     *
     * @param car id
     * @return Car
     */
    @GetMapping(value = "{id}")
    @PreAuthorize("hasRole('LEASE') or hasRole('BROKER')")
    public Car findOne(@PathVariable Long id) {
        Car car = carRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Car with id %d not found", id)));

        log.info(String.format("Found car with id %d", id));
        return car;
    }

    /**
     * Creates a new car.
     * Car Brand and Net Price fields must be populated
     *
     * @param Car
     * @return
     */
    @PostMapping()
    @PreAuthorize("hasRole('LEASE')")
    public ResponseEntity<Object> create(@RequestBody Car car) {
        try {
            carService.createCar(car);
        } catch (InputMismatchException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        log.info(String.format("Created new car with id %d", car.getId()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(car.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates a car if it exists.
     * Car Brand and Net Price fields must be populated
     *
     * @param Car
     * @return
     */
    @PutMapping(value = "{id}")
    @PreAuthorize("hasRole('LEASE')")
    public ResponseEntity<Object> update(@RequestBody Car updatedCar, @PathVariable long id) {
        try {
            carService.updateCar(updatedCar, id);
        } catch (NotFoundException | InputMismatchException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        log.info(String.format("Updated car %d", id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes a car if it exists
     *
     * @param Car id
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('LEASE')")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        Car Car = carRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Car with id %d not found", id)));
        carRepository.delete(Car);
        log.info(String.format("Deleted car %d", id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Returns a list of all revisions that have taken place for any fields on the car
     *
     * @param car id
     * @return List<Car>
     */
    @GetMapping(value = "/history/{id}")
    @PreAuthorize("hasRole('LEASE') or hasRole('BROKER')")
    public List<Car> getRevisions(@PathVariable Long id) {
        Car car = carRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Car with id %d not found", id)));
        List<Car> carRevisions = carHistoryRepository.getCarRevisions(car);
        log.info(String.format("Found history for car %d", id));
        return carRevisions;
    }
}
