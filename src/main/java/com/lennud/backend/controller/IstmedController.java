package com.lennud.backend.controller;

import com.lennud.backend.model.Istmed;
import com.lennud.backend.repository.IstmedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/istmed")
public class IstmedController {

    @Autowired
    private IstmedRepository istmedRepository;

    @GetMapping("/byLennuk/{lennuk}")
    public List<Istmed> getSeatsByAircraft(@PathVariable String lennuk) {
        return istmedRepository.findByLennuk(lennuk);
    }
}
