package uk.claritygroup.kotlin.respository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import uk.claritygroup.kotlin.entity.Metrics
interface MetricsRepositoryKotlin: JpaRepository<Metrics,Long>, JpaSpecificationExecutor<Metrics>
