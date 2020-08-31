package com.alexbakker.carlease.service;

import com.alexbakker.carlease.TestUtil;
import com.alexbakker.carlease.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.InputMismatchException;

public class CustomerServiceTest {

    private final CustomerService victim = new CustomerService();

    @Test
    public void validateContactDetails() {
        victim.validateCustomer(TestUtil.createDefaultCustomer());
    }

    @Test
    public void validateContactDetailsWrongPhoneNumber() {
        Customer customer = TestUtil.createDefaultCustomer();
        customer.setPhoneNumber("12345890");

        Assertions.assertThrows(InputMismatchException.class, () -> {
            victim.validateCustomer(customer);
        });
    }

    @Test
    public void validateContactDetailsWrongEmail() {
        Customer customer = TestUtil.createDefaultCustomer();
        customer.setEmailAddress("invalid.email@address.longsuffix");

        Assertions.assertThrows(InputMismatchException.class, () -> {
            victim.validateCustomer(customer);
        });
    }
}