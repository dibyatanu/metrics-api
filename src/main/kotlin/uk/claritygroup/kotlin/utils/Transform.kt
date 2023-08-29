package uk.claritygroup.kotlin.utils

import uk.claritygroup.kotlin.entity.Metrics
import uk.claritygroup.kotlin.model.request.CreateMetrics
import uk.claritygroup.kotlin.model.request.UpdateMetrics
import uk.claritygroup.kotlin.model.response.Metrics as MetricsResponse

fun CreateMetrics.toEntity()=
    Metrics(
        system = this.system!!,
        name = this.name!!,
        date = this.date.convertToLocalDateAndTime()!!,
        value = this.value
    )

fun UpdateMetrics.toEntity()=
    Metrics(
        system = this.system!!,
        name = this.name!!,
        date = this.date.convertToLocalDateAndTime()!!,
        value = this.value!!
    )

fun Metrics.toModel()=
    CreateMetrics(
        system=this.system,
        name= this.name,
        date= this.date.convertToUnixTimeStamp(),
        value=this.value
    )

fun Metrics.toResponse()=
    MetricsResponse(
        system = this.system,
        name = this.name,
        date = this.date.convertToUnixTimeStamp(),
        value = this.value

    )
