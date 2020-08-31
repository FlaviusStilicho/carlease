package com.alexbakker.carlease.config;


import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import com.alexbakker.carlease.model.payloads.CreateContractRequest;
import com.alexbakker.carlease.repository.CarRepository;
import com.alexbakker.carlease.repository.ContractHistoryRepository;
import com.alexbakker.carlease.repository.ContractRepository;
import com.alexbakker.carlease.repository.CustomerRepository;
import com.alexbakker.carlease.service.CarService;
import com.alexbakker.carlease.service.ContractService;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.InputMismatchException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/contracts")
@RestController
public class ContractController {
    
    @Autowired
    ContractRepository contractRepository;
    @Autowired
    ContractHistoryRepository contractHistoryRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ContractService contractService;

    /**
     * Lists all contracts
     * @return List<Contract>
     */
    @GetMapping()
    @PreAuthorize("hasRole('LEASE') or hasRole('BROKER')")
    public List<Contract> findAll() {
        log.info("Listing all contracts");
        return contractRepository.findAll();
    }

    /**
     * Finds a contract by ID
     * @param contract id
     * @return Contract
     */
    @GetMapping(value = "{id}")
    @PreAuthorize("hasRole('LEASE') or hasRole('BROKER')")
    public Contract findOne(@PathVariable long id) {
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Contract with id %d not found", id)));
        log.info(String.format("Found contract with id %d", id));
        return contract;
    }

    /**
     * Creates a new contract and assigns the car and customer with the provided ids to it.
     * contract fields startdate, enddate, mileage and interest rate must be populated.
     * @param CreateContractRequest (Contract, carId, customerId)
     * @return contract URI
     */
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasRole('LEASE')")
    public ResponseEntity<Object> create(@RequestBody CreateContractRequest request) throws NotFoundException {
        try {
            contractService.createContract(request);
        } catch (NotFoundException | InputMismatchException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        log.info(String.format("Created new contract with id %d", request.getContract().getId()));
        URI location =  ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(request.getContract().getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates the fields of a contract. Does not change the car or customer fields in the contract. While it is
     * possible to enter a different customer ort car in the request, these fields are ignored.
     * @param updated Contract
     * @param contractId
     * @return
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('LEASE')")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody Contract updatedContract) {
        try {
            contractService.updateContract(updatedContract, id);
        } catch (InputMismatchException | NotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        log.info(String.format("Updated contract %d", id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Provides an endpoint for closing a contract. The contact's enddate is set to today, and the car associated
     * with the contract is unassigned.
     * @param contrac iId
     * @return
     */
    @PutMapping("/end/{contractId}")
    @PreAuthorize("hasRole('BROKER')")
    public ResponseEntity<Object> endContract(@PathVariable long contractId) {
        Contract contract = contractRepository.findById(contractId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Contract with id %d not found", contractId)));

        contract.endNow();
        contractRepository.save(contract);
        log.info(String.format("Ended contract %d"), contractId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes a contract
     * @param contract id
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('LEASE')")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        Contract Contract = contractRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Contract with id %d not found", id)));
        contractRepository.delete(Contract);
        log.info(String.format("Deleted contract %d", id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Calculates the montly premiums for a given Customer. If the customer has multiple contract, it returns the premiums
     * for each contract in a seperate entry.
     * @param contract id
     * @return A hashmap with the id of the customer's contract(s) and leaserate(s) in EUR with 2 decimals
     */
    @GetMapping("/leaserate/{customerId}")
    @PreAuthorize("hasRole('BROKER')")
    public  Map<Long, BigDecimal> calculatePremiums(@PathVariable long customerId){
        Customer customer = customerRepository.findById(customerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Customer with id %d not found", customerId)));
        List<Contract> contracts = contractRepository.findByCustomer(customer).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("No contracts found for customer %d", customerId)));

        Map<Long, BigDecimal> premiums = new LinkedHashMap<>();
        for (Contract contract : contracts){
            premiums.put(contract.getId(), contractService.calculateLeaseRate(contract));
        }
        return premiums;
    }

    /**
     * Returns a list of all revisions that have taken place for any fields on the contract
     * @param contract id
     * @return List<Contract>
     */
    @GetMapping("/history/{id}")
    @PreAuthorize("hasRole('LEASE') or hasRole('BROKER')")
    public List<Contract> getRevisions(@PathVariable long id) {
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Contract with id %d not found", id)));
        List<Contract> contractRevisions = contractHistoryRepository.getContractRevisions(contract);
        log.info(String.format("Found history for contract %d", id));
        return contractRevisions;
    }
}
