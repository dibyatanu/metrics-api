package uk.claritygroup.kotlin.model.request

import jakarta.validation.constraints.NotNull

data class CreateMetrics (
    @field:NotNull
    override val system: String? =null,
    @field:NotNull
    override val name: String?= null,
    val date: Long?= System.currentTimeMillis(),
    override val value: Int=1
): MetricsRequest(system,name,value)

