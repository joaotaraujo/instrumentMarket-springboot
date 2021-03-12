package com.joao.musicalMarket.repository;

import com.joao.musicalMarket.entity.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//JpaRepository have a lot of DB methods
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {

    //with optional we can use SECURITY methods
    //(verify if instrument is empty, if exists, etc)
    Optional<Instrument> findByName(String name);

}
