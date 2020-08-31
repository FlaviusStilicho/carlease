package com.alexbakker.carlease;

import com.alexbakker.carlease.model.Car;
import com.alexbakker.carlease.model.Contract;
import com.alexbakker.carlease.model.Customer;
import com.alexbakker.carlease.Security.RoleType;
import com.alexbakker.carlease.Security.Role;
import com.alexbakker.carlease.Security.User;
import com.alexbakker.carlease.model.payloads.CreateContractRequest;
import com.alexbakker.carlease.repository.CarRepository;
import com.alexbakker.carlease.repository.ContractRepository;
import com.alexbakker.carlease.repository.CustomerRepository;
import com.alexbakker.carlease.repository.RoleRepository;
import com.alexbakker.carlease.repository.UserRepository;
import com.alexbakker.carlease.service.ContractService;
import javassist.NotFoundException;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

@Component
public class DemoData {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContractService contractService;
    @Autowired
    PasswordEncoder encoder;

    @EventListener
    public void appReady(ApplicationReadyEvent event) throws NotFoundException {

        Customer customer1 = new Customer(null, "Luke Skywalker","l.skywalker@rebelalliance.com",
                "1234567890","Water Farm","1","1234AB","Tatooine");
        Customer customer2 = new Customer(null, "Obi-Wan Kenobi","generalkenobi@jediorder.com",
                "2345678901", "Old Hut","1","2345QW","Tatooine");
        Customer customer3 = new Customer(null, "Princess Leia","l.skywalker@rebelalliance.com",
                "3456789012", "Tantive","4","3456WE","Deep space");
        Customer customer4 = new Customer(null, "Han Solo","han.solo@kesselrun.com"
                , "4567890123", "Jabba's Basement", "1","4567ER","Tatooine");

        customerRepository.saveAll(List.of(customer1, customer2, customer3, customer4));

        Car car1 = new Car(null,"Incom","X-Wing Starfighter", "1", 1,
                BigDecimal.valueOf(50), BigDecimal.valueOf(40000), BigDecimal.valueOf(40000), false);
        Car car2 = new Car(null,"Incom","Y-Wing Bomber", "1", 1,
                BigDecimal.valueOf(40), BigDecimal.valueOf(40000), BigDecimal.valueOf(40000), false);
        Car car3= new Car(null,"Corellian Engineering Corporation","C-90 Corvette", "1", 20,
                BigDecimal.valueOf(30), BigDecimal.valueOf(40000), BigDecimal.valueOf(40000), false);
        Car car4= new Car(null,"Sienar Fleet Systems","TIE Fighter", "1", 1,
                BigDecimal.valueOf(20), BigDecimal.valueOf(40000), BigDecimal.valueOf(40000), false);

        carRepository.saveAll(List.of(car1, car2, car3, car4));

        Contract contract1 = new Contract(null, null, null, YearMonth.of(2015, 01), YearMonth.of(2022, 01), 60000, 5.5d);
        Contract contract2 = new Contract(null, null, null, YearMonth.of(2017, 03), YearMonth.of(2021, 05), 80000, 4.5d);
        contractService.createContract(new CreateContractRequest(contract1, customer1.getId(), car1.getId()));
        contractService.createContract(new CreateContractRequest(contract2, customer2.getId(), car2.getId()));

        System.out.println(contractRepository.findById(1l).get().getId());

        Role broker = new Role(RoleType.ROLE_BROKER);
        Role lease = new Role(RoleType.ROLE_LEASE);
        roleRepository.save(broker);
        roleRepository.save(lease);

        User user1 = new User("test_broker", encoder.encode("test1"));
        User user2 = new User("test_lease", encoder.encode("test2"));
        user1.setRoles(Set.of(broker));
        user2.setRoles(Set.of(lease));

        userRepository.save(user1);
        userRepository.save(user2);
    }

}