package uk.claritygroup.kotlin.model.request

import jakarta.validation.constraints.NotNull

data class UpdateMetrics(
    @field:NotNull(message = "System must not be null")
    override val system: String?=null,
    @field:NotNull(message = "Name must not be null")
    override val name: String?=null,
    @field:NotNull(message = "Date must not be null")
    var date: Long? = null,
    override val value: Int? =1
): MetricsRequest(system,name,value)
