package uk.claritygroup.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.repository.MetricsRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceTest {
    @Mock
    private MetricsRepository metricsRepository;
    @InjectMocks
    private MetricsService metricsService;
    @Captor
    private ArgumentCaptor<Specification> specificationArgumentCaptor;

    @Nested
    class GetMetricsByCriteria{
        @Test
        @DisplayName("should call metrics repository")
        public void getMetricsByCriteria(){
            metricsService.getMetricsByCriteria(
                    "system", Optional.ofNullable("name"),Optional.ofNullable(1499070300000L),Optional.ofNullable(1499070300000L));
           verify(metricsRepository).findAll(any(Specification.class));
        }
    }
}
