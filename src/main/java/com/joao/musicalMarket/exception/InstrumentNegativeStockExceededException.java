package com.joao.musicalMarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InstrumentNegativeStockExceededException extends Exception {

    public InstrumentNegativeStockExceededException(Long id, int quantityToDecrement) {
        super(String.format("Instruments with %s ID to decrement informed exceeds the min stock capacity: %s", id, quantityToDecrement));
    }
}