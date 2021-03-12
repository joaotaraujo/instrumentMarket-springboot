package com.joao.musicalMarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InstrumentAlreadyRegisteredException extends Exception{

    public InstrumentAlreadyRegisteredException(String instrumentName) {
        super(String.format("Instrument with name %s already registered in the system.", instrumentName));
    }
}