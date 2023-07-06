package uk.claritygroup.service;

import jakarta.persistence.EntityNotFoundException;
import mockit.MockUp;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import uk.claritygroup.BaseTest;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.exception.BadRequestException;
import uk.claritygroup.repository.MetricsRepository;
import uk.claritygroup.utility.DateAndTimeUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceTest extends BaseTest{
    @Mock
    private MetricsRepository metricsRepository;
    @InjectMocks
    private MetricsService metricsService;
    @Captor
    private ArgumentCaptor<MetricsEntity> metricsEntityArgumentCaptor;
    private  final MetricsEntity metricsEntityWithValueOf1 = MetricsEntity
            .builder().id(1L).system("system").name("name").date(LocalDateTime.of(1,1,1,1,1)).value(1).build();
    private  final MetricsEntity metricsEntityWithValueOf4 = MetricsEntity
            .builder().id(1L).system("system").name("name").date(LocalDateTime.of(1,1,1,1,1)).value(4).build();
  @BeforeEach
    public void setUp(){
    new MockUp<DateAndTimeUtil>(){
        @mockit.Mock
        public LocalDateTime convertFromUnixTime(final Optional<Long> unixTime){
            return LocalDateTime.of(1,1,1,1,1);
        }
        @mockit.Mock
        public Long convertToUnixTime(final LocalDateTime localDateTime){
            return 1L;
        }
    };
  }
    @Nested
    class GetMetricsByCriteria  {
        @Test
        @DisplayName("should call metrics repository findAll")
        public void getMetricsByCriteria(){
            when(metricsRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(metricsEntityWithValueOf1));
            var actualResponse=metricsService.getMetricsByCriteria(
                    "system", Optional.ofNullable("name"),Optional.ofNullable(1499070300000L),Optional.ofNullable(1499070300000L));
            assertThat(actualResponse.size()).isEqualTo(1);
            verify(metricsRepository).findAll(any(Specification.class));
        }
    }
    @Nested
    class GetMetricsById{
        @Test
        @DisplayName("should call metrics repository findById")
        public void getMetricsById(){
            when(metricsRepository.findById(1L)).thenReturn(Optional.of(metricsEntityWithValueOf1));
            var actualResponse= metricsService.getMetrics(1l);
            verify(metricsRepository).findById(1l);
            }
        @Test
        @DisplayName("should throw resource not found exception")
        public void getMetricsNotFound(){
            when(metricsRepository.findById(1L)).thenReturn(Optional.empty());
            var exception=Assertions.assertThrows(EntityNotFoundException.class,() ->{
                metricsService.getMetrics(1l);
            });
           assertThat(exception.getMessage()).isEqualTo("The specified metric was not found");
            verify(metricsRepository, times(0)).save(any());
            }
    }

    @Nested
    class CreateMetrics{
        @Test
        @DisplayName("should call mertics repository save")
        public void saveMetrics(){
          metricsService.createMetrics(metricsModelWithAllFields);
            verify(metricsRepository).save(metricsEntityArgumentCaptor.capture());
            assertThat(metricsEntityArgumentCaptor.getValue()).extracting("system","name","date","value")
                    .isEqualTo(Arrays.asList("system","name", LocalDateTime.of(1,1,1,1,1),2));
        }
    }
    @Nested
    class UpdateMetrics{
        @Test
        @DisplayName("should update existing metrics")
        public void shouldUpdateExistingMetric(){
            when(metricsRepository.findById(1L)).thenReturn(Optional.of(metricsEntityWithValueOf1));
            metricsService.updateMetrics(1L,updateMetrics);
            verify(metricsRepository).save(metricsEntityArgumentCaptor.capture());
            assertThat(metricsEntityArgumentCaptor.getValue()).extracting("value")
                    .isEqualTo(3);
        }
        @Test
        @DisplayName("should update existing metrics value by 1 if value is not supplied")
        public void shouldUpdateExistingMetricsValueByOne(){
            when(metricsRepository.findById(1L)).thenReturn(Optional.of(metricsEntityWithValueOf1));
            metricsService.updateMetrics(1L,updateMetricsWithMissingValue);
            verify(metricsRepository).save(metricsEntityArgumentCaptor.capture());
            assertThat(metricsEntityArgumentCaptor.getValue()).extracting("value")
                    .isEqualTo(2);
        }
        @Test
        @DisplayName("should throw resource not found exception")
        public void getMetricsNotFound(){
            when(metricsRepository.findById(1L)).thenReturn(Optional.empty());
            var exception=Assertions.assertThrows(EntityNotFoundException.class,() ->{
                metricsService.updateMetrics(1l,updateMetrics);
            });
            assertThat(exception.getMessage()).isEqualTo("The specified metric was not found");
            verify(metricsRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("should throw 400 not supplied or is invalid, or system or name does not match the existing metric")
        public void throwBadRequestWhenUpdateRequestDoesNotMatch(){
            when(metricsRepository.findById(1L)).thenReturn(Optional.ofNullable(metricsEntityWithValueOf1));
            var exception=Assertions.assertThrows(BadRequestException.class,() ->{
                metricsService.updateMetrics(1l, updateMetricsWithNonExistingSystem);
            });
            assertThat(exception.getMessage()).isEqualTo("A required parameter was not supplied or is invalid, or system or name does not match the existing metric");
            verify(metricsRepository, times(0)).save(any());
        }
    }
    @Nested
    class GetMetricsSummary{
        @Test
        @DisplayName("with all params should return sum of all values")
        public void getMetricsWithAllParams(){
            when(metricsRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(metricsEntityWithValueOf1,metricsEntityWithValueOf4));
            var actualResponse=metricsService.getMetricsSummary(
                    "system", Optional.ofNullable("name"),Optional.ofNullable(1499070300000L),Optional.ofNullable(1499070300000L));
            assertThat(actualResponse).extracting("system","name","from","to","value")
                    .isEqualTo(Arrays.asList("system","name",1499070300000L,1499070300000L,5));
            verify(metricsRepository).findAll(any(Specification.class));
        }
        @Test
        @DisplayName("with name param should return sum of all values")
        public void getMetricsWithName(){
            when(metricsRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(metricsEntityWithValueOf1,metricsEntityWithValueOf4));
            var actualResponse=metricsService.getMetricsSummary(
                    "system", Optional.ofNullable("name"),Optional.empty(),Optional.empty());
            assertThat(actualResponse).extracting("system","name","from","to","value")
                    .isEqualTo(Arrays.asList("system","name",null,null,5));
            verify(metricsRepository).findAll(any(Specification.class));
        }
        @Test
        @DisplayName("with fromDate param should return sum of all values")
        public void getMetricsWithFromDate(){
            when(metricsRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(metricsEntityWithValueOf1,metricsEntityWithValueOf4));
            var actualResponse=metricsService.getMetricsSummary(
                    "system", Optional.empty(),Optional.ofNullable(1499070300000L),Optional.empty());
            assertThat(actualResponse).extracting("system","name","from","to","value")
                    .isEqualTo(Arrays.asList("system",null,1499070300000L,null,5));
            verify(metricsRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("with toDate param should return sum of all values")
        public void getMetricsWithToDate(){
            when(metricsRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(metricsEntityWithValueOf1,metricsEntityWithValueOf4));
            var actualResponse=metricsService.getMetricsSummary(
                    "system", Optional.empty(),Optional.empty(),Optional.ofNullable(1499070300000L));
            assertThat(actualResponse).extracting("system","name","from","to","value")
                    .isEqualTo(Arrays.asList("system",null,null,1499070300000L,5));
            verify(metricsRepository).findAll(any(Specification.class));
        }


    }
}
