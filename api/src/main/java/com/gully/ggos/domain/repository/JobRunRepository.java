package com.gully.ggos.domain.repository;

import com.gully.ggos.domain.entity.JobRun;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRunRepository extends JpaRepository<JobRun, UUID> {
}
