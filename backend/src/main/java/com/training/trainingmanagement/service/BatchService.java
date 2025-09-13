package com.training.trainingmanagement.service;

import com.training.trainingmanagement.entity.Batch;
import com.training.trainingmanagement.repository.BatchRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
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
        batchRepository.deleteById(id);
    }
}
