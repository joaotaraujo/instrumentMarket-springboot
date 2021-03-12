package com.joao.musicalMarket.entity;

import com.joao.musicalMarket.enums.InstrumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data                   //implements getters and setters
@Entity                 //describes the entity (setting primary key types and mapping all atributes to db)
@NoArgsConstructor      //generates a no-args constructor
@AllArgsConstructor     //implements a constructor for us
public class Instrument {

    //id unico do instrumento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //name nao pode ser null e tem que ser unico
    @Column(nullable=false, unique = true)
    private String name;

    @Column(nullable=false)
    private String brand;

    @Column(nullable=false)
    private int max;

    @Column(nullable=false)
    private int min;

    @Column(nullable=false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstrumentType type;
}
