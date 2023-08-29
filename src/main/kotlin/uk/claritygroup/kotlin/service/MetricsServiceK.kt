package uk.claritygroup.kotlin.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import uk.claritygroup.kotlin.exception.BadRequestException
import uk.claritygroup.kotlin.model.request.UpdateMetrics as UpdateMetricsRequest
import uk.claritygroup.kotlin.model.request.CreateMetrics as CreateMetricsRequest
import uk.claritygroup.kotlin.model.response.Metrics as MetricsResponse
import uk.claritygroup.kotlin.entity.Metrics as MetricsEntity
import uk.claritygroup.kotlin.respository.*
import uk.claritygroup.kotlin.utils.convertToLocalDateAndTime
import uk.claritygroup.kotlin.utils.toEntity
import uk.claritygroup.kotlin.utils.toResponse
import uk.claritygroup.kotlin.model.response.MetricsSummary


@Service
class MetricsServiceK(private val repository: MetricsRepositoryKotlin) {

    fun getMetricsListByFilter(system: String,name: String?, fromDate: Long?,toDate:Long? ):List<MetricsResponse> {
       val listOfMetric= repository.findAll(MetricsQuery(
           system= system,
           name= name,
           fromDate= fromDate,
           toDate= toDate
       ).toSpec())

     return  when{
           listOfMetric.isEmpty() ->  throw EntityNotFoundException("No metrics found matching the criteria")
            else ->  listOfMetric.map {
               it.toResponse()
           }
       }
    }

    fun getMetricsById(id: Long) = repository.findById(id).orElseThrow{ EntityNotFoundException("The specified metric was not found")} .toResponse()

    fun createMetrics(createMetricsRequest: CreateMetricsRequest)= repository.save(createMetricsRequest.toEntity()).toResponse()

    fun updateMetrics(id: Long,updateMetricsRequest: UpdateMetricsRequest): MetricsResponse{
        val metricsEntity=repository.findById(id).orElseThrow{ EntityNotFoundException("The specified metric was not found")}

        val updatedMetricsEntity=  metricsEntity.takeIf {it.isMatch(updateMetricsRequest)} ?.let {
            repository.save(it.copy(
                value = it.value+updateMetricsRequest.value!!
            ))
         } ?: throw BadRequestException("A required system or name or date does not match the existing metric")

        return updatedMetricsEntity.toResponse()
    }

    fun getMetricsSummary(system: String,name: String?, fromDate: Long?,toDate:Long? ): MetricsSummary{
        val sumOfValue= repository.findAll(MetricsQuery(
            system= system,
            name= name,
            fromDate= fromDate,
            toDate= toDate
        ).toSpec()).let {
                listOfMetrics ->
                  listOfMetrics.sumOf { it.value }
        }
        return MetricsSummary(system= system,name = name, from = fromDate,to = toDate , value = sumOfValue)

    }

 private fun MetricsEntity.isMatch(updateMetricsRequest: UpdateMetricsRequest): Boolean {
     return (this.system == updateMetricsRequest.system) and (this.name == updateMetricsRequest.name) and (this.date == updateMetricsRequest.date.convertToLocalDateAndTime())
 }



}
