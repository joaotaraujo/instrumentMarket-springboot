package com.joao.musicalMarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InstrumentNotFoundException extends Exception{

    public InstrumentNotFoundException(String instrumentName) {
        super(String.format("Instrument with name %s not found in the system.", instrumentName));
    }

    public InstrumentNotFoundException(Long id) {
        super(String.format("Instrument with id %s not found in the system.", id));
    }
}
