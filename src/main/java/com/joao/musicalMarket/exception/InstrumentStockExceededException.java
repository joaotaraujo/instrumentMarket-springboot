package com.joao.musicalMarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InstrumentStockExceededException extends Exception {

    public InstrumentStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Instruments with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}