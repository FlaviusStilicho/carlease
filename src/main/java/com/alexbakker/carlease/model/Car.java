package com.alexbakker.carlease.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.envers.Audited;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "car")
@Audited
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @NonNull
    private String carBrand;
    private String model;
    private String modelVersion;
    private Integer numberOfDoors;
    private BigDecimal co2EmissionsPerct;
    private BigDecimal grossPrice;
    @NonNull
    private BigDecimal netPrice;
    @Setter
    private boolean currentlyAssigned;
}
