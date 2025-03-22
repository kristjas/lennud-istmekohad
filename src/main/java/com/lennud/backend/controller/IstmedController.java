package com.lennud.backend.controller;

import com.lennud.backend.model.Istmed;
import com.lennud.backend.service.IstmedRandom;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/istmed")
@CrossOrigin(origins = "*")
public class IstmedController {
    private final IstmedRandom istmedRandom;

    public IstmedController(IstmedRandom istmedRandom) {
        this.istmedRandom = istmedRandom;
    }

    @GetMapping("/{lennuk}")
    public List<Istmed> getIstmedByLennuk(@PathVariable String lennuk) {
        return istmedRandom.getSeatsByLennuk(lennuk);
    }
}
