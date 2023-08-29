package uk.claritygroup.kotlin.resources

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import uk.claritygroup.kotlin.model.request.CreateMetrics
import uk.claritygroup.kotlin.model.request.UpdateMetrics
import uk.claritygroup.kotlin.model.response.MetricsSummary
import uk.claritygroup.kotlin.service.MetricsServiceK
import uk.claritygroup.kotlin.model.response.Metrics as MetricsResponse

@RestController
@RequestMapping("/kotlin", produces = [MediaType.APPLICATION_JSON_VALUE])
@Validated
class MetricsResourceK(private val metricsService: MetricsServiceK) {
    @Tag(name = "Kotlin")
    @Operation(summary = "Retrieves a list of metrics.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "A list of metrics that meet the search criteria",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MetricsResponse::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "A required parameter was not supplied or is invalid",
            content = [Content(mediaType = "application/json")]
        )]
    )
    @GetMapping("/metrics")
    fun getListOfMetrics(@Valid @RequestParam(name = "system") @NotBlank(message ="A required parameter was not supplied or is invalid")  system: String,
                         @RequestParam(name = "name" , required = false) name: String?,
                         @RequestParam(name = "from", required = false) fromDate: Long?,
                         @RequestParam(name = "to", required = false) toDate: Long?): List<MetricsResponse>{
        return metricsService.getMetricsListByFilter(system,name,fromDate,toDate)
    }

    @Tag(name = "Kotlin")
    @Operation(summary = "Retrieves a single metric specified by {id}.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "The requested metric",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MetricsResponse::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "A required parameter was not supplied or is invalid",
            content = [Content(mediaType = "application/json")]
        ), ApiResponse(
                responseCode = "404",
                description = "The specified metric was not found",
                content = [Content(mediaType = "application/json")]
        )]
    )
    @GetMapping("/metrics/{id}")
    fun getMetrics(@PathVariable id: Long) = metricsService.getMetricsById(id)

    @Tag(name = "Kotlin")
    @Operation(summary = "Creates a new metric, with a default value of 1, and a default date of the current date/time.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "The metric was recorded",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MetricsResponse::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "A required parameter was not supplied or is invalid",
            content = [Content(mediaType = "application/json")]
        )]
    )
    @PostMapping("/metrics")
    fun createMetrics(@Valid @RequestBody createMetricsRequest: CreateMetrics) =
        metricsService.createMetrics(createMetricsRequest)

    @Tag(name = "Kotlin")
    @Operation(summary = "Updates the specified metric with the supplied metric. If value is not supplied, the existing value is incremented by 1.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "The metric was recorded",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MetricsResponse::class))]
        ), ApiResponse(
            responseCode = "400",
            description = "A required parameter was not supplied or is invalid",
            content = [Content(mediaType = "application/json")]
        ), ApiResponse(
            responseCode = "404",
            description = "The specified metric was not found",
            content = [Content(mediaType = "application/json")]
        )]
    )
    @PutMapping("/metrics/{id}")
    fun updateMetrics(@PathVariable id: Long,@Valid @RequestBody updateMetricsRequest: UpdateMetrics) =
        metricsService.updateMetrics(id,updateMetricsRequest)

    @Tag(name = "Kotlin")
    @Operation(summary = "Retrieves a metric summary.")
    @ApiResponses(
            value = [ApiResponse(
                responseCode = "200",
                description = "The metric was recorded",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = MetricsSummary::class))]
            ), ApiResponse(
                responseCode = "400",
                description = "A required parameter was not supplied or is invalid",
                content = [Content(mediaType = "application/json")]
            )]
        )
    @GetMapping("/metricsummary")
    fun getMetricsSummary(@Valid @RequestParam(name = "system") @NotBlank(message ="A required parameter was not supplied or is invalid")  system: String,
                          @RequestParam(name = "name" , required = false) name: String?,
                          @RequestParam(name = "from", required = false) fromDate: Long?,
                          @RequestParam(name = "to", required = false) toDate: Long?):MetricsSummary{

        return metricsService.getMetricsSummary(system,name,fromDate,toDate)
    }
}