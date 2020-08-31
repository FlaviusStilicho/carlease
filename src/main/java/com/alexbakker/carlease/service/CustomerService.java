package com.alexbakker.carlease.service;

import com.alexbakker.carlease.model.Customer;
import com.alexbakker.carlease.repository.CustomerRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b");

    /**
     * validates if s new PersonalInfo objects contrains a correctly formatted phone number (must contain 10 digits)
     * and a correct email address (one or more alphanumerical characters, an @, one or more alphanumerical characters,
     * a dot, 2-4 letters)
     *
     * @param personalInfo
     * @throws InputMismatchException
     */
    public void validateCustomer(Customer customer) throws InputMismatchException {
        if (customer.getName().isBlank()) {
            throw new InputMismatchException("Invalid personal info: Name cannot be blank");
        }
        Matcher matcher1 = PHONE_NUMBER_PATTERN.matcher(customer.getPhoneNumber());
        if (!matcher1.matches()) {
            throw new InputMismatchException("Invalid personal info: Phone number must contain 10 digits");
        }
        Matcher matcher2 = EMAIL_PATTERN.matcher(customer.getEmailAddress());
        if (!matcher2.matches()) {
            throw new InputMismatchException("Invalid personal info: Incorrectly formatted email address");
        }
    }

    public void createCustomer(Customer customer){
        validateCustomer(customer);
        customerRepository.save(customer);
    }

    public void updateCustomer(Customer updatedCustomer, long id) throws NotFoundException {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Customer with id %d not found", id)));

        validateCustomer(updatedCustomer);
        updatedCustomer.setId(id);
        customerRepository.save(updatedCustomer);
    }
}
