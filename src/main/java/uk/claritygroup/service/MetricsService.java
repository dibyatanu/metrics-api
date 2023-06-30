package uk.claritygroup.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.MetricsSummary;
import uk.claritygroup.model.UpdateMetrics;
import uk.claritygroup.repository.MetricsRepository;
import uk.claritygroup.specifications.MetricsSpecifications;
import uk.claritygroup.utility.DateAndTimeUtil;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class MetricsService {
    private final MetricsRepository metricsRepository;

    public MetricsService(@Autowired final MetricsRepository metricsRepository ){
       this.metricsRepository=metricsRepository;
    }

    public List<MetricsEntity> getMetricsByCriteria(final String system,final Optional<String> name,final Optional<Long> fromDate,final Optional<Long> toDate ) {
        log.info("getting list of metrics by criteria");
       return metricsRepository.findAll(
               Specification.where(MetricsSpecifications.systemEquals(system))
                             .and(MetricsSpecifications.nameEquals(name))
                             .and(MetricsSpecifications.dateIsOnAndAfter(Optional.ofNullable(DateAndTimeUtil.convertFromUnixTime(fromDate))))
                             .and(MetricsSpecifications.dateIsBefore(Optional.ofNullable(DateAndTimeUtil.convertFromUnixTime(toDate)))));

    }
   public CreateMetrics getMetrics(final Long id){
       log.info("getting metrics by id");
       var returnedMetrics= metricsRepository.findById(id).orElseThrow(()->new EntityNotFoundException("The specified metric was not found"));
       return new CreateMetrics(
               returnedMetrics.getSystem(),
               returnedMetrics.getName(),
               DateAndTimeUtil.convertToUnixTime(returnedMetrics.getDate()),
               returnedMetrics.getValue());
    }
    public MetricsEntity createMetrics(final CreateMetrics metricsModel){
        var metricsEntity= MetricsEntity
                                        .builder()
                                        .system(metricsModel.getSystem())
                                        .name(metricsModel.getName())
                                        .date(DateAndTimeUtil.convertFromUnixTime(Optional.ofNullable(metricsModel.getDate())))
                                        .value(metricsModel.getValue()).build();
        log.info("Creating metrics");
       return metricsRepository.save(metricsEntity);
    }

    public MetricsEntity updateMetrics(final Long id, final UpdateMetrics updateMetrics){
        var returnedMetrics= metricsRepository.findById(id).orElseThrow(()->new EntityNotFoundException("The specified metric was not found"));
       var updatedMetrics= MetricsEntity
                                       .builder()
                                       .id(returnedMetrics.getId())
                                       .system(updateMetrics.getSystem())
                                       .name(updateMetrics.getName())
                                       .date(DateAndTimeUtil.convertFromUnixTime(Optional.ofNullable(updateMetrics.getDate())))
                                       .value(returnedMetrics.getValue()+updateMetrics.getValue()).build();
        log.info("updating metrics");
        return metricsRepository.save(updatedMetrics);
    }

    public MetricsSummary getMetricsSummary(final String system,final Optional<String> name,final Optional<Long> fromDate,final Optional<Long> toDate){
        log.info("getting metrics summary list");
       var metrics= metricsRepository.findAll(
               Specification.where(MetricsSpecifications.systemEquals(system))
                            .and(MetricsSpecifications.nameEquals(name))
                            .and(MetricsSpecifications.dateIsOnAndAfter(Optional.ofNullable(DateAndTimeUtil.convertFromUnixTime(fromDate))))
                            .and(MetricsSpecifications.dateIsBefore(Optional.ofNullable(DateAndTimeUtil.convertFromUnixTime(toDate)))));
        var totalValue=metrics.stream().mapToInt(metric -> metric.getValue()).sum();
        return MetricsSummary.builder().system(system)
                                       .name(name.isPresent()?name.get():null)
                                       .from(fromDate.isPresent()?fromDate.get():null)
                                       .to(toDate.isPresent()?toDate.get():null).value(totalValue).build();
    }

}
