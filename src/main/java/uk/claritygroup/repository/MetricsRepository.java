package uk.claritygroup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uk.claritygroup.entity.MetricsEntity;
@Repository
public interface MetricsRepository extends JpaRepository<MetricsEntity,Long> , JpaSpecificationExecutor<MetricsEntity> {


}
