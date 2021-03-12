package com.joao.musicalMarket.controller;

import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.dto.QuantityDTO;
import com.joao.musicalMarket.exception.InstrumentAlreadyRegisteredException;
import com.joao.musicalMarket.exception.InstrumentNegativeStockExceededException;
import com.joao.musicalMarket.exception.InstrumentNotFoundException;
import com.joao.musicalMarket.exception.InstrumentStockExceededException;
import com.joao.musicalMarket.service.InstrumentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;


//indicates that its a controller class, will lead with data processing (json and xml)
@RestController
//requestMapping defines the name of ur api
@RequestMapping("/api/v1/instruments")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class InstrumentController implements InstrumentControllerDocs {

    private final InstrumentService instrumentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrumentDTO createInstrument(@RequestBody @Valid InstrumentDTO instrumentDTO)
                                          throws InstrumentAlreadyRegisteredException {
        return instrumentService.createInstrument(instrumentDTO);
    }

    @GetMapping("/{name}")
    public InstrumentDTO findByName(@PathVariable String name) throws InstrumentNotFoundException {
        return instrumentService.findByName(name);
    }

    @GetMapping
    public List<InstrumentDTO> listInstruments() {
        return instrumentService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws InstrumentNotFoundException {
        instrumentService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public InstrumentDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO)
            throws InstrumentNotFoundException, InstrumentStockExceededException {
        return instrumentService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public InstrumentDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO)
            throws InstrumentNotFoundException, InstrumentNegativeStockExceededException {
        return instrumentService.decrement(id, quantityDTO.getQuantity());
    }
}
