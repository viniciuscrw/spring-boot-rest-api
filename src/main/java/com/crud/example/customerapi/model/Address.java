package com.crud.example.customerapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String street;

    @NotNull
    private int number;

    @NotNull
    @Pattern(regexp="[\\d]{8}")
    private String zipCode;

    private String complement;

    @NotBlank
    private String neighbourhood;

    @NotBlank
    private String city;

    @NotBlank
    @Pattern(regexp="[\\w]{2}")
    private String uf;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    @JsonIgnoreProperties("id")
    private Customer customer;
}
