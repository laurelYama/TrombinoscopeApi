package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Cycle;
import com.esiitech.trombinoscope_api.repository.CycleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CycleService {

    private final CycleRepository cycleRepository;

    public CycleService(CycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }

    public List<Cycle> getAllCycle() {
        return cycleRepository.findAll();
    }

    public Cycle getCycleById(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cycle non trouvé"));
    }

    public Cycle createCycle(Cycle cycle) {
        return cycleRepository.save(cycle);
    }

    public Cycle updateCycle(Long id, Cycle newCycle) {
        return cycleRepository.findById(id).map(cycle -> {
            cycle.setNiveau(newCycle.getNiveau());
            return cycleRepository.save(cycle);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cycle non trouvé"));
    }
}
