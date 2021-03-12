package com.joao.musicalMarket.controller;

import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.exception.InstrumentAlreadyRegisteredException;
import com.joao.musicalMarket.exception.InstrumentNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("This api leads with a musical market with a stock of instruments!")
public interface InstrumentControllerDocs {

    @ApiOperation(value = "Instrument creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success instrument creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    InstrumentDTO createInstrument(InstrumentDTO instrumentDTO) throws InstrumentAlreadyRegisteredException;

    @ApiOperation(value = "Returns Instrument found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success Instrument found in the system"),
            @ApiResponse(code = 404, message = "Instrument with given name not found.")
    })
    InstrumentDTO findByName(@PathVariable String name) throws InstrumentNotFoundException;

    @ApiOperation(value = "Returns a list of all Instrument registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all Instruments registered in the system"),
    })
    List<InstrumentDTO> listInstruments();

    @ApiOperation(value = "Delete a Instrument found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success Instrument deleted in the system"),
            @ApiResponse(code = 404, message = "Instrument with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws InstrumentNotFoundException;
}