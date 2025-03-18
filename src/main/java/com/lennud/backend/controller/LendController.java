package com.lennud.backend.controller;

import com.lennud.backend.model.Lend;
import com.lennud.backend.repository.LendRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:5173")
public class LendController {

    private final LendRepository lendRepository;

    public LendController(LendRepository lendRepository) {
        this.lendRepository = lendRepository;
    }

    @GetMapping
    public List<Lend> getAllFlights() {
        return lendRepository.findAll();
    }
}
