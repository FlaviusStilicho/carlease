package com.alexbakker.carlease.service;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import com.alexbakker.carlease.model.payloads.CreateContractRequest;
import com.alexbakker.carlease.repository.CarRepository;
import com.alexbakker.carlease.repository.ContractHistoryRepository;
import com.alexbakker.carlease.repository.ContractRepository;
import com.alexbakker.carlease.repository.CustomerRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.InputMismatchException;

@Service
public class ContractService {

    @Autowired
    ContractRepository contractRepository;
    @Autowired
    ContractHistoryRepository contractHistoryRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ContractService contractService;
    @Autowired
    CarRepository carRepository;
    @Autowired
    CarService carService;

    /**
     * Validates if a new or edited contract has values for Car, Start Date, End Date, Interest Rate and Yearly Mileage
     * @param contract
     * @throws InputMismatchException
     */
    public void validateContract(Contract contract) throws InputMismatchException {
        if (contract.getCurrentContractStartDate() == null) {
            throw new InputMismatchException("Invalid contract: Start date cannot be empty");
        }
        if (contract.getCurrentContractEndDate() == null) {
            throw new InputMismatchException("Invalid contract: End date cannot be empty");
        }
        if (contract.getInterestRate() == null) {
            throw new InputMismatchException("Invalid contract: Interest rate cannot be empty");
        }
        if (contract.getYearlyMileageKm() == null) {
            throw new InputMismatchException("Invalid contract: Yearly mileage cannot be empty");
        }
    }

    /**
     * Calculates lease rate using formula ((( mileage / 12 ) * duration ) / Nett price)
     * + ((( Interest rate / 100 ) * Nett price) / 12 )
     * @param customer
     * @return leaserate in EUR in 2 decimals
     */
    public BigDecimal calculateLeaseRate(Contract contract) {
        Car car = contract.getCar();

        BigDecimal contractDurationInMonths = BigDecimal.valueOf(contract.getContractDuration());
        BigDecimal monthlyMileage = BigDecimal.valueOf(contract.getYearlyMileageKm() / 12);
        BigDecimal yearlyInterestRate = BigDecimal.valueOf(contract.getInterestRate() / 100);
        BigDecimal netPrice = car.getNetPrice();

        BigDecimal monthlyPremium =
                monthlyMileage.multiply(contractDurationInMonths)
                        .divide(netPrice, 2, RoundingMode.HALF_UP);

        BigDecimal monthlyInterest =
                netPrice.multiply(yearlyInterestRate)
                        .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        return monthlyPremium.add(monthlyInterest);
    }

    @Transactional
    public void createContract(CreateContractRequest request) throws NotFoundException {
        Car car = carRepository.findById(request.getCarId()).orElseThrow(() ->
                new NotFoundException(String.format("Car with id %d not found", request.getCarId())));
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() ->
                new NotFoundException(String.format("Customer with id %d not found", request.getCustomerId())));
        Contract contract = request.getContract();
        validateContract(contract);
        if (car.isCurrentlyAssigned()){
            throw new InputMismatchException(String.format("Car with id %d is already assigned to a contract.",
                    request.getCarId()));
        }
        contract.setCar(car);
        car.setCurrentlyAssigned(true);
        contract.setCustomer(customer);
        contractRepository.save(contract);
    }

    public void updateContract(Contract updatedContract, long id) throws NotFoundException {
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Contract with id %d not found", id)));

        contractService.validateContract(updatedContract);
        updatedContract.setId(contract.getId());
        updatedContract.setCustomer(contract.getCustomer());
        updatedContract.setCar(contract.getCar());
        contractRepository.save(updatedContract);
    }

}
