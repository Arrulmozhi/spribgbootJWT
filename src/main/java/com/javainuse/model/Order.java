package com.javainuse.model;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
public class Order {

    @Id
    private String id;

    @NotNull(message = "RefId cannot be null")
    private String refId;

    @NotNull(message = "User email cannot be null")
    private String userEmail;

    @NotNull(message = "User code cannot be null")
    private Integer userCode;

    @NotNull(message = "Items cannot be null")
    @Size(min = 1, message = "At least one item is required")
    private List<Item> items;

    @NotNull(message = "Cost cannot be null")
    private double cost;

   
}

