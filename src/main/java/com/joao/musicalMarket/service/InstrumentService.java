package com.joao.musicalMarket.service;

import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.entity.Instrument;
import com.joao.musicalMarket.exception.InstrumentAlreadyRegisteredException;
import com.joao.musicalMarket.exception.InstrumentNegativeStockExceededException;
import com.joao.musicalMarket.exception.InstrumentNotFoundException;
import com.joao.musicalMarket.exception.InstrumentStockExceededException;
import com.joao.musicalMarket.mapper.InstrumentMapper;
import com.joao.musicalMarket.repository.InstrumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//indicates this class will be used by spring to use in controller
@Service
//implements a constructor for us
@AllArgsConstructor(onConstructor = @__(@Autowired))
//implements ''regras de negocio''
public class InstrumentService {

    //with @Autowired we don't need write the code above (dependency injection)
    //@Autowired
    //public InstrumentService(InstrumentRepository instrumentRepository){
    //  this.instrumentRepository = instrumentRepository
    //}

    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper = InstrumentMapper.INSTANCE;

    public InstrumentDTO createInstrument(InstrumentDTO instrumentDTO) throws InstrumentAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(instrumentDTO.getName());
        Instrument instrument = instrumentMapper.toModel(instrumentDTO);
        Instrument savedInstrument = instrumentRepository.save(instrument);
        return instrumentMapper.toDTO(savedInstrument);
    }

    public InstrumentDTO findByName(String name) throws InstrumentNotFoundException {
        Instrument foundInstrument = instrumentRepository.findByName(name)
                .orElseThrow(() -> new InstrumentNotFoundException(name));
        return instrumentMapper.toDTO(foundInstrument);
    }

    public List<InstrumentDTO> listAll() {
        return instrumentRepository.findAll()
                .stream()
                .map(instrumentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws InstrumentNotFoundException {
        verifyIfExists(id);
        instrumentRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws InstrumentAlreadyRegisteredException {
        Optional<Instrument> optSavedInstrument = instrumentRepository.findByName(name);
        if (optSavedInstrument.isPresent()) {
            throw new InstrumentAlreadyRegisteredException(name);
        }
    }

    private Instrument verifyIfExists(Long id) throws InstrumentNotFoundException {
        return instrumentRepository.findById(id)
                .orElseThrow(() -> new InstrumentNotFoundException(id));
    }

    public InstrumentDTO increment(Long id, int quantityToIncrement) throws InstrumentNotFoundException, InstrumentStockExceededException {
        Instrument instrumentToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + instrumentToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= instrumentToIncrementStock.getMax()) {
            instrumentToIncrementStock.setQuantity(instrumentToIncrementStock.getQuantity() + quantityToIncrement);
            Instrument incrementedInstrumentStock = instrumentRepository.save(instrumentToIncrementStock);
            return instrumentMapper.toDTO(incrementedInstrumentStock);
        }
        throw new InstrumentStockExceededException(id, quantityToIncrement);
    }

    public InstrumentDTO decrement(Long id, int quantityToDecrement) throws InstrumentNotFoundException, InstrumentNegativeStockExceededException {
        Instrument instrumentToDecrementStock = verifyIfExists(id);
        int quantityAfterDecrement = instrumentToDecrementStock.getQuantity() - quantityToDecrement;
        if (quantityAfterDecrement >= instrumentToDecrementStock.getMin()) {
            instrumentToDecrementStock.setQuantity(instrumentToDecrementStock.getQuantity() - quantityToDecrement);
            Instrument incrementedInstrumentStock = instrumentRepository.save(instrumentToDecrementStock);
            return instrumentMapper.toDTO(incrementedInstrumentStock);
        }
        throw new InstrumentNegativeStockExceededException(id, quantityToDecrement);
    }
}
