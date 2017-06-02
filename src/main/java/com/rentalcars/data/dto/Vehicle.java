package com.rentalcars.data.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Sergey on 02-Jun-17.
 */
@Data
public class Vehicle {

    private String sipp;
    private String name;
    private BigDecimal price;
    private String supplier;
    private BigDecimal rating;
}
