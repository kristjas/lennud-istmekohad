package com.lennud.backend.seeds;

import com.lennud.backend.model.Istmed;
import com.lennud.backend.model.Lend;
import com.lennud.backend.repository.IstmedRepository;
import com.lennud.backend.repository.LendRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class dbSeeder {

    @Bean
    public CommandLineRunner seedDatabase(LendRepository lendRepository, IstmedRepository istmedRepository) {
        //Täidame andmebaasi
        return args -> {
            if (lendRepository.count() == 0) {
                System.out.println("lisame lennud");

                Lend lend1 = new Lend(null, "Tallinn", LocalDate.of(2025, 3, 26), LocalTime.of(8, 0), LocalTime.of(10, 30), 150.00, "Boeing");
                Lend lend2 = new Lend(null, "London", LocalDate.of(2025, 3, 27), LocalTime.of(12, 15), LocalTime.of(13, 15), 200.00, "Boeing");
                Lend lend3 = new Lend(null, "Berliin", LocalDate.of(2025, 3, 29), LocalTime.of(18, 15), LocalTime.of(23, 0), 180.00, "Boeing");

                lendRepository.saveAll(List.of(lend1, lend2, lend3));
                System.out.println("lennud lisatud");


                if (istmedRepository.count() == 0) {


                    List<Istmed> seats = List.of(
                            new Istmed(null, 1, true, true, false, false, "Boeing"),
                            new Istmed(null, 2, true, false, false, false, "Boeing"),
                            new Istmed(null, 3, true, false, false, false, "Boeing"),
                            new Istmed(null, 4, true, true, false, false, "Boeing"),
                            new Istmed(null, 5, false, true, false, false, "Boeing"),
                            new Istmed(null, 6, false, false, false, false, "Boeing"),
                            new Istmed(null, 7, false, false, false, false, "Boeing"),
                            new Istmed(null, 8, false, true, false, false, "Boeing"),
                            new Istmed(null, 9, false, true, false, false, "Boeing"),
                            new Istmed(null, 10, false, false, false, false, "Boeing"),
                            new Istmed(null, 11, false, false, false, false, "Boeing"),
                            new Istmed(null, 12, false, true, false, false, "Boeing"),
                            new Istmed(null, 13, false, true, false, false, "Boeing"),
                            new Istmed(null, 14, false, false, false, false, "Boeing"),
                            new Istmed(null, 15, false, false, false, false, "Boeing"),
                            new Istmed(null, 16, false, true, false, false, "Boeing"),
                            new Istmed(null, 17, false, true, false, false, "Boeing"),
                            new Istmed(null, 18, false, false, false, false, "Boeing"),
                            new Istmed(null, 19, false, false, true, false, "Boeing"),
                            new Istmed(null, 20, false, true, true, false, "Boeing")
                    );

                    istmedRepository.saveAll(seats);
                    System.out.println("istmed lisatud");
                }
            } else {
                System.out.println("andmed juba varem lisatud");
            }
        };
    }
}
