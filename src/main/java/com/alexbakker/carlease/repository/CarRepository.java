package com.alexbakker.carlease.repository;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

}
