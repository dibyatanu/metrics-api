package uk.claritygroup.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.MetricsSummary;
import uk.claritygroup.model.UpdateMetrics;
import uk.claritygroup.service.MetricsService;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
@Slf4j
public class MerticsResource {
    private final MetricsService metricsService;
    public MerticsResource(@Autowired final MetricsService metricsService){
        this.metricsService=metricsService;
    }

    @GetMapping(value="/metrics",produces =MediaType.APPLICATION_JSON_VALUE )
    public List<MetricsEntity> getMetricsList(@RequestParam(name="system") @NotBlank final  String system,
                                             @RequestParam(name="name", required = false) final Optional<String> name,
                                             @RequestParam(name="from", required = false) final Optional<Long> from,
                                             @RequestParam(name="to", required = false) final Optional<Long> to){
        log.info("REQUEST_RECEIVED: get list of metrics");
        return metricsService.getMetricsByCriteria(system,name,from,to);
    }

    @GetMapping(value = "/metrics/{id}", produces =MediaType.APPLICATION_JSON_VALUE )
    public CreateMetrics getMetrics(@PathVariable("id") final Long id){
        return metricsService.getMetrics(id);
    }

    @PostMapping(value="/metrics",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public  MetricsEntity createMetrics(@Valid  @RequestBody final CreateMetrics metricsModel){
        log.info("REQUEST_RECEIVED: creating metrics");
        return metricsService.createMetrics(metricsModel);
    }

    @PutMapping(value = "/metrics/{id}", produces =MediaType.APPLICATION_JSON_VALUE )
    public MetricsEntity updatedMetrics(@PathVariable("id") final Long id,@RequestBody final UpdateMetrics updateMetrics)
    {
        log.info("REQUEST_RECEIVED: update metrics");
        return metricsService.updateMetrics(id,updateMetrics);
    }

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
