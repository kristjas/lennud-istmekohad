package com.lennud.backend.repository;

import com.lennud.backend.model.Istmed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IstmedRepository extends JpaRepository<Istmed, Long> {
    List<Istmed> findByLennuk(String lennuk);
}
