package com.javainuse.model;

import com.mongodb.lang.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

	@NotNull(message = "Item code cannot be null")
    private String itemCode;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity should be at least 1")
    private Integer quantity;

}

