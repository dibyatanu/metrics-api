package uk.claritygroup.kotlin.model.request

import jakarta.validation.constraints.NotNull

 open class MetricsRequest (
    open val system: String? =null,
    open val name: String? =null,
    open val value: Int? =1
)
