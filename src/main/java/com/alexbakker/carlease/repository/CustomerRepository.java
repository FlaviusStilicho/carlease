package com.alexbakker.carlease.repository;

import com.alexbakker.carlease.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
