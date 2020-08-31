package com.alexbakker.carlease.model.payloads;

import com.alexbakker.carlease.model.Contract;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateContractRequest {
    Contract contract;
    long customerId;
    long carId;
}
