package com.joao.musicalMarket.service;

import com.joao.musicalMarket.builder.InstrumentDTOBuilder;
import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.entity.Instrument;
import com.joao.musicalMarket.exception.InstrumentAlreadyRegisteredException;
import com.joao.musicalMarket.exception.InstrumentNegativeStockExceededException;
import com.joao.musicalMarket.exception.InstrumentNotFoundException;
import com.joao.musicalMarket.exception.InstrumentStockExceededException;
import com.joao.musicalMarket.mapper.InstrumentMapper;
import com.joao.musicalMarket.repository.InstrumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstrumentServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private InstrumentRepository instrumentRepository;

    private InstrumentMapper instrumentMapper = InstrumentMapper.INSTANCE;

    @InjectMocks
    private InstrumentService instrumentService;

    @Test
    void whenInstrumentInformedThenItShouldBeCreated() throws InstrumentAlreadyRegisteredException {
        // given
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedSavedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        // when
        when(instrumentRepository.findByName(expectedInstrumentDTO.getName())).thenReturn(Optional.empty());
        when(instrumentRepository.save(expectedSavedInstrument)).thenReturn(expectedSavedInstrument);

        //then
        InstrumentDTO createdInstrumentDTO = instrumentService.createInstrument(expectedInstrumentDTO);

        assertThat(createdInstrumentDTO.getId(), is(equalTo(expectedInstrumentDTO.getId())));
        assertThat(createdInstrumentDTO.getName(), is(equalTo(expectedInstrumentDTO.getName())));
        assertThat(createdInstrumentDTO.getQuantity(), is(equalTo(expectedInstrumentDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredInstrumentInformedThenAnExceptionShouldBeThrown() {
        // given
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument duplicatedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        // when
        when(instrumentRepository.findByName(expectedInstrumentDTO.getName())).thenReturn(Optional.of(duplicatedInstrument));

        // then
        assertThrows(InstrumentAlreadyRegisteredException.class, () -> instrumentService.createInstrument(expectedInstrumentDTO));
    }

    @Test
    void whenValidInstrumentNameIsGivenThenReturnAInstrument() throws InstrumentNotFoundException {
        // given
        InstrumentDTO expectedFoundInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedFoundInstrument = instrumentMapper.toModel(expectedFoundInstrumentDTO);

        // when
        when(instrumentRepository.findByName(expectedFoundInstrument.getName())).thenReturn(Optional.of(expectedFoundInstrument));

        // then
        InstrumentDTO foundInstrumentDTO = instrumentService.findByName(expectedFoundInstrumentDTO.getName());

        assertThat(foundInstrumentDTO, is(equalTo(expectedFoundInstrumentDTO)));
    }

    @Test
    void whenNotRegisteredInstrumentNameIsGivenThenThrowAnException() {
        // given
        InstrumentDTO expectedFoundInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        // when
        when(instrumentRepository.findByName(expectedFoundInstrumentDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(InstrumentNotFoundException.class, () -> instrumentService.findByName(expectedFoundInstrumentDTO.getName()));
    }

    @Test
    void whenListInstrumentIsCalledThenReturnAListOfInstruments() {
        // given
        InstrumentDTO expectedFoundInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedFoundInstrument = instrumentMapper.toModel(expectedFoundInstrumentDTO);

        //when
        when(instrumentRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundInstrument));

        //then
        List<InstrumentDTO> foundListInstrumentsDTO = instrumentService.listAll();

        assertThat(foundListInstrumentsDTO, is(not(empty())));
        assertThat(foundListInstrumentsDTO.get(0), is(equalTo(expectedFoundInstrumentDTO)));
    }

    @Test
    void whenListInstrumentIsCalledThenReturnAnEmptyListOfInstruments() {
        //when
        when(instrumentRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<InstrumentDTO> foundListInstrumentsDTO = instrumentService.listAll();

        assertThat(foundListInstrumentsDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenAInstrumentShouldBeDeleted() throws InstrumentNotFoundException{
        // given
        InstrumentDTO expectedDeletedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedDeletedInstrument = instrumentMapper.toModel(expectedDeletedInstrumentDTO);

        // when
        when(instrumentRepository.findById(expectedDeletedInstrumentDTO.getId())).thenReturn(Optional.of(expectedDeletedInstrument));
        doNothing().when(instrumentRepository).deleteById(expectedDeletedInstrumentDTO.getId());

        // then
        instrumentService.deleteById(expectedDeletedInstrumentDTO.getId());

        verify(instrumentRepository, times(1)).findById(expectedDeletedInstrumentDTO.getId());
        verify(instrumentRepository, times(1)).deleteById(expectedDeletedInstrumentDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementInstrumentStock() throws InstrumentNotFoundException, InstrumentStockExceededException {
        //given
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        //when
        when(instrumentRepository.findById(expectedInstrumentDTO.getId())).thenReturn(Optional.of(expectedInstrument));
        when(instrumentRepository.save(expectedInstrument)).thenReturn(expectedInstrument);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedInstrumentDTO.getQuantity() + quantityToIncrement;

        // then
        InstrumentDTO incrementedInstrumentDTO = instrumentService.increment(expectedInstrumentDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedInstrumentDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedInstrumentDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        when(instrumentRepository.findById(expectedInstrumentDTO.getId())).thenReturn(Optional.of(expectedInstrument));

        int quantityToIncrement = 80;
        assertThrows(InstrumentStockExceededException.class, () -> instrumentService.increment(expectedInstrumentDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        when(instrumentRepository.findById(expectedInstrumentDTO.getId())).thenReturn(Optional.of(expectedInstrument));

        int quantityToIncrement = 45;
        assertThrows(InstrumentStockExceededException.class, () -> instrumentService.increment(expectedInstrumentDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(instrumentRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(InstrumentNotFoundException.class, () -> instrumentService.increment(INVALID_BEER_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementInstrumentStock() throws InstrumentNotFoundException, InstrumentNegativeStockExceededException {
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        when(instrumentRepository.findById(expectedInstrumentDTO.getId())).thenReturn(Optional.of(expectedInstrument));
        when(instrumentRepository.save(expectedInstrument)).thenReturn(expectedInstrument);

        int quantityToDecrement = 3;
        int expectedQuantityAfterDecrement = expectedInstrumentDTO.getQuantity() - quantityToDecrement;
        InstrumentDTO incrementedInstrumentDTO = instrumentService.decrement(expectedInstrumentDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedInstrumentDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyInstrumentStock() throws InstrumentNotFoundException, InstrumentNegativeStockExceededException {
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        when(instrumentRepository.findById(expectedInstrumentDTO.getId())).thenReturn(Optional.of(expectedInstrument));
        when(instrumentRepository.save(expectedInstrument)).thenReturn(expectedInstrument);

        int quantityToDecrement = 16;
        int expectedQuantityAfterDecrement = expectedInstrumentDTO.getQuantity() - quantityToDecrement;
        InstrumentDTO decrementedInstrumentDTO = instrumentService.decrement(expectedInstrumentDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(decrementedInstrumentDTO.getMin()));
        assertThat(expectedQuantityAfterDecrement, equalTo(decrementedInstrumentDTO.getQuantity()));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        InstrumentDTO expectedInstrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        Instrument expectedInstrument = instrumentMapper.toModel(expectedInstrumentDTO);

        when(instrumentRepository.findById(expectedInstrumentDTO.getId())).thenReturn(Optional.of(expectedInstrument));

        int quantityToDecrement = 19;
        assertThrows(InstrumentNegativeStockExceededException.class, () -> instrumentService.decrement(expectedInstrumentDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 55;

        when(instrumentRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        assertThrows(InstrumentNotFoundException.class, () -> instrumentService.decrement(INVALID_BEER_ID, quantityToDecrement));
    }
}
