package com.joao.musicalMarket.builder;

import com.joao.musicalMarket.dto.InstrumentDTO;
import com.joao.musicalMarket.enums.InstrumentType;
import lombok.Builder;

//this returns an object already setted
@Builder
public class InstrumentDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Slash Lespaul";

    @Builder.Default
    private String brand = "Gibson";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int min = 1;

    @Builder.Default
    private int quantity = 17;

    @Builder.Default
    private InstrumentType type = InstrumentType.GUITAR;

    public InstrumentDTO toInstrumentDTO() {
        return new InstrumentDTO(id,
                name,
                brand,
                max,
                min,
                quantity,
                type);
    }
}

