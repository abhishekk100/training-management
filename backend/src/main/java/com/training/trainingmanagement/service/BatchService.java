package com.training.trainingmanagement.service;

import com.training.trainingmanagement.entity.Batch;
import com.training.trainingmanagement.entity.TrainerAvailability;
import com.training.trainingmanagement.repository.BatchRepository;
import com.training.trainingmanagement.repository.TrainerAvailabilityRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final TrainerAvailabilityRepository availabilityRepository;
    
    
    public BatchService(BatchRepository batchRepository, TrainerAvailabilityRepository availabilityRepository) {
        this.batchRepository = batchRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Optional<Batch> getBatchById(Integer id) {
        return batchRepository.findById(id);
    }

    @Transactional
    public Batch saveBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    @Transactional
    public void deleteBatch(Integer id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found with id " + id));

        // Restore trainer availability
        TrainerAvailability availability = batch.getTrainerAvailability();
        if (availability != null) {
            availability.setStatus("Yes"); // free up the slot
            availabilityRepository.saveAndFlush(availability);
        }

        // Delete the batch
        batchRepository.delete(batch);
    }

}
