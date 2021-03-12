package com.joao.musicalMarket.controller;

import com.joao.musicalMarket.builder.InstrumentDTOBuilder;
import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.dto.QuantityDTO;
import com.joao.musicalMarket.exception.InstrumentNegativeStockExceededException;
import com.joao.musicalMarket.exception.InstrumentNotFoundException;
import com.joao.musicalMarket.exception.InstrumentStockExceededException;
import com.joao.musicalMarket.service.InstrumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.joao.musicalMarket.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class InstrumentControllerTest {

    private static final String INSTRUMENT_API_URL_PATH = "/api/v1/instruments";
    private static final long VALID_INSTRUMENT_ID = 1L;
    private static final long INVALID_INSTRUMENT_ID = 2L;
    private static final String INSTRUMENT_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String INSTRUMENT_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private InstrumentService instrumentService;

    @InjectMocks
    private InstrumentController instrumentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(instrumentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAInstrumentIsCreated() throws Exception {
        // given
        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        // when
        when(instrumentService.createInstrument(instrumentDTO)).thenReturn(instrumentDTO);

        // then
        mockMvc.perform(post(INSTRUMENT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(instrumentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(instrumentDTO.getName())))
                .andExpect(jsonPath("$.brand", is(instrumentDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(instrumentDTO.getType().toString())));
    }

//    @Test
//    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
//        // given
//        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
//        instrumentDTO.setBrand(null);
//
//        // then
//        mockMvc.perform(post(INSTRUMENT_API_URL_PATH)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(instrumentDTO)))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        //when
        when(instrumentService.findByName(instrumentDTO.getName())).thenReturn(instrumentDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(INSTRUMENT_API_URL_PATH + "/" + instrumentDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(instrumentDTO.getName())))
                .andExpect(jsonPath("$.brand", is(instrumentDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(instrumentDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        //when
        when(instrumentService.findByName(instrumentDTO.getName())).thenThrow(InstrumentNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(INSTRUMENT_API_URL_PATH + "/" + instrumentDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithInstrumentsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        //when
        when(instrumentService.listAll()).thenReturn(Collections.singletonList(instrumentDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(INSTRUMENT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(instrumentDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(instrumentDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(instrumentDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutInstrumentsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        //when
        when(instrumentService.listAll()).thenReturn(Collections.singletonList(instrumentDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(INSTRUMENT_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();

        //when
        doNothing().when(instrumentService).deleteById(instrumentDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(INSTRUMENT_API_URL_PATH + "/" + instrumentDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(InstrumentNotFoundException.class).when(instrumentService).deleteById(INVALID_INSTRUMENT_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(INSTRUMENT_API_URL_PATH + "/" + INVALID_INSTRUMENT_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        instrumentDTO.setQuantity(instrumentDTO.getQuantity() + quantityDTO.getQuantity());

        when(instrumentService.increment(VALID_INSTRUMENT_ID, quantityDTO.getQuantity())).thenReturn(instrumentDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(INSTRUMENT_API_URL_PATH + "/" + VALID_INSTRUMENT_ID + INSTRUMENT_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(instrumentDTO.getName())))
                .andExpect(jsonPath("$.brand", is(instrumentDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(instrumentDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(instrumentDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(45)
                .build();

        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        instrumentDTO.setQuantity(instrumentDTO.getQuantity() + quantityDTO.getQuantity());

        when(instrumentService.increment(VALID_INSTRUMENT_ID, quantityDTO.getQuantity())).thenThrow(InstrumentStockExceededException.class);

        mockMvc.perform(patch(INSTRUMENT_API_URL_PATH + "/" + VALID_INSTRUMENT_ID + INSTRUMENT_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidInstrumentIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(instrumentService.increment(INVALID_INSTRUMENT_ID, quantityDTO.getQuantity())).thenThrow(InstrumentNotFoundException.class);
        mockMvc.perform(patch(INSTRUMENT_API_URL_PATH + "/" + INVALID_INSTRUMENT_ID + INSTRUMENT_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        instrumentDTO.setQuantity(instrumentDTO.getQuantity() + quantityDTO.getQuantity());

        when(instrumentService.decrement(VALID_INSTRUMENT_ID, quantityDTO.getQuantity())).thenReturn(instrumentDTO);

        mockMvc.perform(patch(INSTRUMENT_API_URL_PATH + "/" + VALID_INSTRUMENT_ID + INSTRUMENT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(instrumentDTO.getName())))
                .andExpect(jsonPath("$.brand", is(instrumentDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(instrumentDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(instrumentDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(18)
                .build();

        InstrumentDTO instrumentDTO = InstrumentDTOBuilder.builder().build().toInstrumentDTO();
        instrumentDTO.setQuantity(instrumentDTO.getQuantity() + quantityDTO.getQuantity());

        when(instrumentService.decrement(VALID_INSTRUMENT_ID, quantityDTO.getQuantity())).thenThrow(InstrumentNegativeStockExceededException.class);

        mockMvc.perform(patch(INSTRUMENT_API_URL_PATH + "/" + VALID_INSTRUMENT_ID + INSTRUMENT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidInstrumentIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(20)
                .build();

        when(instrumentService.decrement(INVALID_INSTRUMENT_ID, quantityDTO.getQuantity())).thenThrow(InstrumentNotFoundException.class);
        mockMvc.perform(patch(INSTRUMENT_API_URL_PATH + "/" + INVALID_INSTRUMENT_ID + INSTRUMENT_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}

