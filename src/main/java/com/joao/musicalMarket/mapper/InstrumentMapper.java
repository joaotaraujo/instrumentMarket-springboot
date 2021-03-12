package com.joao.musicalMarket.mapper;

import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.entity.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

//interface used to transfer Instrument to InstrumentDAO
//to use it u will need lombok and mapstruct dependencies
@Mapper
public interface InstrumentMapper {

    InstrumentMapper INSTANCE = Mappers.getMapper(InstrumentMapper.class);

    Instrument toModel(InstrumentDTO instrumentDTO);

    InstrumentDTO toDTO(Instrument instrument);


}
