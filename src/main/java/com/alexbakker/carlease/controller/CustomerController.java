package com.alexbakker.carlease.controller;

import com.alexbakker.carlease.model.Customer;
import com.alexbakker.carlease.repository.CustomerHistoryRepository;
import com.alexbakker.carlease.repository.CustomerRepository;
import com.alexbakker.carlease.service.ContractService;
import com.alexbakker.carlease.service.CustomerService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerHistoryRepository customerHistoryRepository;
    @Autowired
    private CustomerService customerService;


    /**
     * Lists all customers
     * @return List<Customer>
     */
    @GetMapping()
    @PreAuthorize("hasRole('BROKER')")
    public List<Customer> findAll() {
        log.info("Listing all customers");
        return customerRepository.findAll();
    }

    /**
     * Finds a customer by ID
     * @param customer id
     * @return Customer
     */
    @GetMapping(value = "{id}")
    @PreAuthorize("hasRole('BROKER')")
    public Customer findOne(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Customer with id %d not found", id)));
        log.info(String.format("Found customer with id %d", id));
        return customer;
    }

    /**
     * Creates a new customer.
     * All fields must be populated.
     * Email address must match regex "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b"
     * Phone number must match regex "^\\d{10}$"
     * @param Customer
     * @return
     */
    @PostMapping()
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Object> create(@RequestBody Customer customer) {
        try {
            customerService.createCustomer(customer);
        } catch (InputMismatchException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        log.info(String.format("Created new customer with id %d", customer.getId()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates a customer if it exists.
     * All fields must be populated.
     * Email address must match regex "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\\b"
     * Phone number must match regex "^\\d{10}$"
     * @param Customer
     * @param customer id
     * @return
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody Customer updatedCustomer) {
        try {
            customerService.updateCustomer(updatedCustomer, id);
        } catch (InputMismatchException | NotFoundException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        log.info(String.format("Updated personal info for customer %d", id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes a customer if it exists
     * @param customer id
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Customer with id %d not found", id)));
        try {
            customerRepository.delete(customer);
        } catch (DataIntegrityViolationException e){
            log.error(e.getMessage());
            return new ResponseEntity<>("Cannot delete this customer because there are still contracts that " +
                    "reference it", HttpStatus.BAD_REQUEST);
        }
        log.info(String.format("Deleted customer %d", id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Returns a list of all revisions that have taken place for any fields on the customer
     * @param customer id
     * @return List<Customer>
     */
    @GetMapping("/history/{id}")
    @PreAuthorize("hasRole('BROKER')")
    public List<Customer> getRevisions(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Customer with id %d not found", id)));

        List<Customer> customerRevisions = customerHistoryRepository.getCustomerRevisions(customer);
        log.info(String.format("Found history for customer %d", id));
        return customerRevisions;
    }

}
