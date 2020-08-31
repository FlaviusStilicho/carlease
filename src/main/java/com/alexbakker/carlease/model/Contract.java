package com.alexbakker.carlease.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "contract")
@Audited
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;
    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private Car car;
    @NonNull
    private YearMonth currentContractStartDate;
    @Setter
    @NonNull
    private YearMonth currentContractEndDate;
    @NonNull
    private Integer yearlyMileageKm;
    @NonNull
    private Double interestRate;


    public long getContractDuration() {
        return ChronoUnit.MONTHS.between(currentContractStartDate, currentContractEndDate);
    }

    public void endNow(){
        if (null != this.getCar()){
            this.setCurrentContractEndDate(YearMonth.now());
            car.setCurrentlyAssigned(false);
            this.setCar(null);
        }
    }
}
