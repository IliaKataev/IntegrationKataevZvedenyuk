package com.ilia.services;

import com.ilia.model.Spare;
import com.ilia.repositories.SpareRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("spareSyncService")
public class SpareSyncService {

    private final SpareRepository repo;

    public SpareSyncService(SpareRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void sync(Spare cms) {

        Spare entity = repo.findById(cms.getSpareCode())
                .orElseGet(Spare::new);

        entity.setSpareCode(cms.getSpareCode());
        entity.setSpareName(cms.getSpareName());
        entity.setSpareDescription(cms.getSpareDescription());
        entity.setSpareType(cms.getSpareType());
        entity.setSpareStatus(cms.getSpareStatus());
        entity.setPrice(cms.getPrice());
        entity.setQuantity(cms.getQuantity());
        entity.setUpdatedAt(cms.getUpdatedAt());
        entity.setLastSeenAt(cms.getLastSeenAt());

        repo.save(entity);
    }
}
