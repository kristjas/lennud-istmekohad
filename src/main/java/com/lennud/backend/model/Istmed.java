package com.lennud.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Istmed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int istekoht;

    private boolean jalaruum;
    private boolean aken;
    private boolean exit;
    private boolean voetud;

    @Column(nullable = false)
    private String lennuk;
}
