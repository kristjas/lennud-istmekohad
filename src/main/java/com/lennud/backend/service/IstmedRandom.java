package com.lennud.backend.service;

import com.lennud.backend.model.Istmed;
import com.lennud.backend.repository.IstmedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class IstmedRandom {
    //paneme kinni suvalised istmed
    private final IstmedRepository istmedRepository;
    private final Random random = new Random();

    public IstmedRandom(IstmedRepository istmedRepository) {
        this.istmedRepository = istmedRepository;
    }



    public List<Istmed> getSeatsByLennuk(String lennuk) {
        List<Istmed> istmed = istmedRepository.findByLennuk(lennuk);


        for (Istmed iste : istmed) {
            if (random.nextInt(10) < 3) {
                iste.setVoetud(true);
            }
        }

        return istmed;
    }
}
