package com.lennud.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sihtkoht;
    private LocalDate kuupäev;
    private LocalTime lennualgusaeg;
    private LocalTime lennulõppaeg;
    private double hind;

    @Column(nullable = false)
    private String lennuk;



}
