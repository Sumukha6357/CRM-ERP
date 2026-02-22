package com.gully.ggos.service;

import com.gully.ggos.domain.entity.KpiSnapshot;
import com.gully.ggos.domain.repository.KpiSnapshotRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class KpiService {
    private final KpiSnapshotRepository kpiSnapshotRepository;

    public KpiService(KpiSnapshotRepository kpiSnapshotRepository) {
        this.kpiSnapshotRepository = kpiSnapshotRepository;
    }

    public List<KpiSnapshot> getLatestByOrg(UUID orgId) {
        return kpiSnapshotRepository.findLatestByOrgId(orgId);
    }
}
