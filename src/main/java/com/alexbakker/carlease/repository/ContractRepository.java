package com.alexbakker.carlease.repository;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<List<Contract>> findByCustomer(Customer customer);

}
