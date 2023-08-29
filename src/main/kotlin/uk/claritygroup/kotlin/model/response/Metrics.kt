package uk.claritygroup.kotlin.model.response

import io.swagger.v3.oas.annotations.media.Schema

data class Metrics(
    @Schema(description = "the name of the system this metric relates to")
    val system: String,
    @Schema(description = "the name of this metric")
    val name: String?= null,
    @Schema(description = "the date/time this metric applies to (in unix time)")
    val date: Long?= null,
    @Schema(description = "the current value of this metric")
    val value: Int=0
)

