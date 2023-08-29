package uk.claritygroup.kotlin.respository

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import uk.claritygroup.kotlin.entity.Metrics
import uk.claritygroup.kotlin.entity.Metrics_
import uk.claritygroup.kotlin.utils.convertToLocalDateAndTime
import java.time.LocalDateTime
data class MetricsQuery(
    val system: String,
    val name: String? = null,
    val fromDate: Long? = null,
    val toDate: Long? = null
)


 fun MetricsQuery.toSpec(): Specification<Metrics> =
              systemEquals(this.system)
              .and(nameEquals(this.name))
              .and(dateOnAndAfter(this.fromDate.convertToLocalDateAndTime()))
              .and(dateBefore(this.toDate.convertToLocalDateAndTime()))

private fun systemEquals(system: String): Specification<Metrics> =
    Specification{ root: Root<Metrics>, _: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
       criteriaBuilder.equal(root.get((Metrics_.system)),system)}

private fun nameEquals(name: String?): Specification<Metrics>? =
      name?.let{
       Specification { root: Root<Metrics>, _: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
           criteriaBuilder.equal(root.get((Metrics_.name)), it)
       }
  }

private fun dateOnAndAfter(date: LocalDateTime?): Specification<Metrics>? =
    date?.let {
        Specification { root: Root<Metrics>, _: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            criteriaBuilder.greaterThanOrEqualTo(root.get((Metrics_.date)), it)
        }
    }

private fun dateBefore(date: LocalDateTime?): Specification<Metrics>? =
    date?.let {
        Specification { root: Root<Metrics>, _: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
            criteriaBuilder.lessThan(root.get((Metrics_.date)), it)
        }
    }

