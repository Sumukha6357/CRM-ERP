package com.gully.ggos.service;

import com.gully.ggos.domain.entity.JobRun;
import com.gully.ggos.domain.entity.KpiSnapshot;
import com.gully.ggos.domain.entity.Org;
import com.gully.ggos.domain.repository.CrmLeadRepository;
import com.gully.ggos.domain.repository.JobRunRepository;
import com.gully.ggos.domain.repository.KpiSnapshotRepository;
import com.gully.ggos.domain.repository.OrgRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KpiScheduler {
    private final JobRunRepository jobRunRepository;
    private final KpiSnapshotRepository kpiSnapshotRepository;
    private final OrgRepository orgRepository;
    private final CrmLeadRepository leadRepository;

    public KpiScheduler(
        JobRunRepository jobRunRepository,
        KpiSnapshotRepository kpiSnapshotRepository,
        OrgRepository orgRepository,
        CrmLeadRepository leadRepository
    ) {
        this.jobRunRepository = jobRunRepository;
        this.kpiSnapshotRepository = kpiSnapshotRepository;
        this.orgRepository = orgRepository;
        this.leadRepository = leadRepository;
    }

    @Scheduled(cron = "0 15 * * * *")
    @Transactional
    public void refreshKpis() {
        JobRun run = new JobRun();
        run.setJobCode("KPI_REFRESH");
        run.setStatus("RUNNING");
        run.setStartedAt(OffsetDateTime.now());
        run = jobRunRepository.save(run);

        try {
            List<Org> orgs = orgRepository.findAll();
            LocalDate snapshotDate = LocalDate.now();
            OffsetDateTime since = OffsetDateTime.now().minusDays(7);
            for (Org org : orgs) {
                long count = leadRepository.countCreatedSince(org.getId(), since);
                if (!kpiSnapshotRepository.existsByOrg_IdAndMetricCodeAndSnapshotDate(org.getId(), "LEADS_7D", snapshotDate)) {
                    KpiSnapshot snapshot = new KpiSnapshot();
                    snapshot.setOrg(org);
                    snapshot.setMetricCode("LEADS_7D");
                    snapshot.setMetricValue(BigDecimal.valueOf(count));
                    snapshot.setSnapshotDate(snapshotDate);
                    kpiSnapshotRepository.save(snapshot);
                }
            }
            run.setStatus("SUCCESS");
            run.setEndedAt(OffsetDateTime.now());
            jobRunRepository.save(run);
        } catch (Exception ex) {
            run.setStatus("FAILED");
            run.setErrorMessage(ex.getMessage());
            run.setEndedAt(OffsetDateTime.now());
            jobRunRepository.save(run);
        }
    }
}
