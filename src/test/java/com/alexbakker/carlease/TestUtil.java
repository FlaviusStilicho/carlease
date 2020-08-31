package com.alexbakker.carlease;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Customer;

import java.math.BigDecimal;

public class TestUtil {

    public static Customer createDefaultCustomer(){
        return new Customer(null, "John Doe","email@address.com","0123456789", "some street", "some number", "some zipcode", "some city");
    }

    public static Car createDefaultCar(){
        return new Car(null, "some brand","some model", "some version", 0, BigDecimal.valueOf(4), BigDecimal.valueOf(50000), BigDecimal.valueOf(50000), true);
    }
}
