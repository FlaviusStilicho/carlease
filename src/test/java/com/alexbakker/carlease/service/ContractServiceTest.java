package com.alexbakker.carlease.service;

import com.alexbakker.carlease.TestUtil;
import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

public class ContractServiceTest {

    private ContractService victim = new ContractService();

    @Test
    public void calculateLeaseRate(){
        Car car = Car.builder().netPrice(new BigDecimal(63000)).build();
        Contract contract = Contract.builder()
                .customer(TestUtil.createDefaultCustomer())
                .car(car)
                .currentContractStartDate(YearMonth.of(2020, 01))
                .currentContractEndDate(YearMonth.of(2025, 01))
                .interestRate(4.5d)
                .yearlyMileageKm(45000).build();

        BigDecimal actual = victim.calculateLeaseRate(contract);

        BigDecimal expected = BigDecimal.valueOf(239.82);
        assertEquals(expected, actual);
    }
}