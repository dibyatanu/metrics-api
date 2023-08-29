package uk.claritygroup.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.MetricsSummary;
import uk.claritygroup.model.UpdateMetrics;
import uk.claritygroup.service.MetricsService;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class MerticsResource {
    private final MetricsService metricsService;
    @Tag(name = "Java")
    @Operation(summary = "Retrieves a list of metrics.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "A list of metrics that meet the search criteria",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation =MetricsEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "A required parameter was not supplied or is invalid",
                    content = @Content)
           }
    )
    @Tag(name = "Java")
    @GetMapping(value="/metrics",produces =MediaType.APPLICATION_JSON_VALUE )
    public List<MetricsEntity> getMetricsList(@RequestParam(name="system") @NotBlank final  String system,
                                             @RequestParam(name="name", required = false) final Optional<String> name,
                                             @RequestParam(name="from", required = false) final Optional<Long> from,
                                             @RequestParam(name="to", required = false) final Optional<Long> to){
        log.info("REQUEST_RECEIVED: get list of metrics");
        return metricsService.getMetricsByCriteria(system,name,from,to);
    }
    @Tag(name = "Java")
    @Operation(summary = "Retrieves a single metric specified by {id}.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested metric",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation =CreateMetrics.class)) }),
            @ApiResponse(responseCode = "400", description = "A required parameter was not supplied or is invalid",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "The specified metric was not found",
                    content = @Content)
    }
    )
    @GetMapping(value = "/metrics/{id}", produces =MediaType.APPLICATION_JSON_VALUE )
    public CreateMetrics getMetrics(@PathVariable("id") final Long id){
        log.info("REQUEST_RECEIVED: get metrics by id");
        return metricsService.getMetrics(id);
    }
    @Tag(name = "Java")
    @Operation(summary = "Creates a new metric, with a default value of 1, and a default date of the current date/time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The metric was recorded",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation =MetricsEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "A required parameter was not supplied or is invalid",
                    content = @Content)
    }
    )
    @PostMapping(value="/metrics",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public  MetricsEntity createMetrics(@Valid  @RequestBody final CreateMetrics metricsModel){
        log.info("REQUEST_RECEIVED: creating metrics");
        return metricsService.createMetrics(metricsModel);
    }
    @Tag(name = "Java")
    @Operation(summary = "Updates the specified metric with the supplied metric. If value is not supplied, the existing value is incremented by 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The metric was recorded",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation =MetricsEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "A required parameter was not supplied or is invalid, or system or name does not match the existing metric",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "The specified metric was not found",
                    content = @Content)
    }
    )
    @PutMapping(value = "/metrics/{id}", produces =MediaType.APPLICATION_JSON_VALUE )
    public MetricsEntity updatedMetrics(@PathVariable("id") final Long id,@RequestBody final UpdateMetrics updateMetrics)
    {
        log.info("REQUEST_RECEIVED: update metrics");
        return metricsService.updateMetrics(id,updateMetrics);
    }
    @Tag(name = "Java")
    @Operation(summary = "Retrieves a metric summary.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The metric was recorded",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation =MetricsSummary.class)) }),
            @ApiResponse(responseCode = "400", description = "A required parameter was not supplied or is invalid",
                    content = @Content)
    }
    )
    @GetMapping(value = "/metricsummary", produces = MediaType.APPLICATION_JSON_VALUE)
    public MetricsSummary getMetricsSummary(@RequestParam(name="system") @NotBlank final  String system,
                                            @RequestParam(name="name", required = false) final Optional<String> name,
                                            @RequestParam(name="from", required = false) final Optional<Long> from,
                                            @RequestParam(name="to", required = false) final Optional<Long> to

    ){
        log.info("REQUEST_RECEIVED: get metrics summary");
        return  metricsService.getMetricsSummary(system,name,from,to);
    }
}
