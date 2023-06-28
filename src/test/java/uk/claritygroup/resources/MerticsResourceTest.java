package uk.claritygroup.resources;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;

import uk.claritygroup.BaseTest;
import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.UpdateMetrics;
import uk.claritygroup.service.MetricsService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MerticsResourceTest extends BaseTest {
    @Mock
    private MetricsService metricsService;
    @InjectMocks
    private MerticsResource merticsResource;
    @Captor
    private ArgumentCaptor<Long> idCaptor;
    @Captor
    private ArgumentCaptor<String> systemCaptor;
    @Captor
    private ArgumentCaptor<Optional<String>> nameCaptor;
    @Captor
    private ArgumentCaptor<Optional<Long>> fromDateCaptor;
    @Captor
    private ArgumentCaptor<Optional<Long>> toDateCaptor;

    @Nested
    class GetMetricsList{
        @Test
        @DisplayName("should call metrics service getMetricsList")
        public void getMetricsList(){
          var metricList= merticsResource.getMetricsList(
                    "system",Optional.ofNullable("name"),Optional.ofNullable(1499070300000L),Optional.ofNullable(1499070300000L));
        verify(metricsService).getMetricsByCriteria(systemCaptor.capture(),nameCaptor.capture(),fromDateCaptor.capture(),toDateCaptor.capture());
        assertThat(systemCaptor.getValue()).isEqualTo("system");
        assertThat(nameCaptor.getValue().get()).isEqualTo("name");
        assertThat(fromDateCaptor.getValue().get()).isEqualTo(1499070300000L);
        assertThat(toDateCaptor.getValue().get()).isEqualTo(1499070300000L);
        }
    }
    @Nested
    class GetMetrics{
        @Test
        @DisplayName("should call metrics service getMetrics")
        public void getMetrics(){
            merticsResource.getMetrics(1l);
            verify(metricsService).getMetrics(idCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(1l);
        }
    }
    @Nested
    class CreateMetric {
        @Test
        @DisplayName("should call metrics service create")
        public void createMetrics(){
            merticsResource.createMetrics(metricsModelWithAllFields);
            var argumentCaptor= ArgumentCaptor.forClass(CreateMetrics.class);
            verify(metricsService).createMetrics(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).isEqualTo(metricsModelWithAllFields);
        }
    }

    @Nested
    class UpdateMetric{
        @Test
        @DisplayName("should call metrics service update")
        public void updateMetrics(){
            merticsResource.updatedMetrics(1l,updateMetrics);
            var argumentCaptor= ArgumentCaptor.forClass(UpdateMetrics.class);
            verify(metricsService).updateMetrics(idCaptor.capture(),argumentCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(1l);
        }
    }

    @Nested
    class GetMetricSummary{
        @Test@DisplayName("should call metrics service summary")
        public void getMetricsSummary(){
            merticsResource.getMetricsSummary(
                    "system",Optional.ofNullable("name"),Optional.ofNullable(1499070300000L),Optional.ofNullable(1499070300000L));
            verify(metricsService).getMetricsSummary(systemCaptor.capture(),nameCaptor.capture(),fromDateCaptor.capture(),toDateCaptor.capture());
            assertThat(systemCaptor.getValue()).isEqualTo("system");
            assertThat(nameCaptor.getValue().get()).isEqualTo("name");
            assertThat(fromDateCaptor.getValue().get()).isEqualTo(1499070300000L);
            assertThat(toDateCaptor.getValue().get()).isEqualTo(1499070300000L);

        }


    }

}
