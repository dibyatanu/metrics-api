package uk.claritygroup.integrationtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.claritygroup.BaseTest;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.MetricsSummary;
import uk.claritygroup.utility.DateAndTimeUtil;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@ActiveProfiles("test")
@Sql( "classpath:test_data/clear_data.sql")
public class MetricsServiceIntegrationTest extends BaseTest {
    @Autowired
    private WebTestClient webTestClient;
    @LocalServerPort
    int port;
    @Nested
    class GetAllMetricsFilteredByQueryParam{
        @Test
        @DisplayName("should retrieve a list of metrics based on system ,name,from and to")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void returnMetricsFilteredBySystemAndName(){
            var from =DateAndTimeUtil.convertToUnixTime(LocalDateTime.now().minusDays(1));
            var to= DateAndTimeUtil.convertToUnixTime(LocalDateTime.now().plusDays(1));
            var actualResponse=webTestClient.get()
                    .uri( builder -> builder.path("/metrics")
                            .queryParam("system","system")
                            .queryParam("name","name2")
                            .queryParam("from",from)
                            .queryParam("to",to)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MetricsEntity.class).returnResult().getResponseBody();
            assertThat(actualResponse.size()).isEqualTo(1);
        }
        @Test
        @DisplayName("should retrieve a list of metrics based on system,to")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void  returnMetricsFilteredBySystemAndFromDate(){
            var to= DateAndTimeUtil.convertToUnixTime(LocalDateTime.now().minusDays(1));
            System.out.println(LocalDateTime.now()+" "+to+" "+port);
            var actualResponse=webTestClient.get()
                    .uri( builder -> builder.path("/metrics")
                            .queryParam("system","system")
                            .queryParam("to",to)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MetricsEntity.class).returnResult().getResponseBody();
            assertThat(actualResponse.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("should retrieve a list of metrics based on system,from")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void  returnMetricsFilteredBySystemAndTomDate(){
            var from =DateAndTimeUtil.convertToUnixTime(LocalDateTime.now().minusDays(1));
            var actualResponse=webTestClient.get()
                    .uri( builder -> builder.path("/metrics")
                            .queryParam("system","system")
                            .queryParam("from",from)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MetricsEntity.class).returnResult().getResponseBody();
            assertThat(actualResponse)
                    .extracting(MetricsEntity::getSystem).isEqualTo(Arrays.asList("system","system"));
            assertThat(actualResponse)
                    .extracting(MetricsEntity::getName).isEqualTo(Arrays.asList("name2","name3"));
            assertThat(actualResponse)
                    .extracting(MetricsEntity::getValue).isEqualTo(Arrays.asList(1,2));
            assertThat(actualResponse.size()).isEqualTo(2);
        }
        @Test
        @DisplayName("should retrieve a list of metrics based on system")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void returnMetricsFilteredBySystem(){
            var actualResponse=webTestClient.get()
                    . uri( builder -> builder.path("/metrics")
                            .queryParam("system","system")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MetricsEntity.class).returnResult().getResponseBody();
            assertThat(actualResponse.size()).isEqualTo(2);
        }
        @Test
        @DisplayName("should return 400 bad request when system is missing")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void badRequest(){
            var actualResponse=webTestClient.get()
                    .uri( builder -> builder.path("/metrics")
                            .queryParam("system","")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class).returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
        }
    }

    @Nested
    class GetMetricById{
        @Test
        @DisplayName("should return metrics by id")
        @Sql("classpath:test_data/seed_data.sql")
        public void shouldReturnMetric(){
            var actualResponse=webTestClient.get()
                    .uri("/metrics/1")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(CreateMetrics.class).returnResult().getResponseBody();
         assertThat(actualResponse).extracting("system").isEqualTo("system");
         assertThat(actualResponse).extracting("system","name","value")
                    .isEqualTo(Arrays.asList("system","name",1));
        }
     @Test
     @DisplayName("should return 404 when id not found")
     @Sql("classpath:test_data/seed_data.sql")
     public void returnNotFound(){
        var actualResponse=webTestClient.get()
                .uri("/metrics/5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat(actualResponse).contains("The specified metric was not found");
    }

     @Test
     @DisplayName("should return 400 bad request")
     @Sql("classpath:test_data/seed_data.sql")
     public void badRequest(){
        var actualResponse=webTestClient.get()
                .uri("/metrics")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
     }
    }

    @Nested
    class CreateMetric{
        @Test
        @DisplayName("should create metrics with all fields")
        public void createMetrics(){
           var actualResponse= webTestClient.post()
                   .uri("/metrics")
                    .bodyValue(metricsModelWithAllFields)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsEntity.class).returnResult().getResponseBody();

           assertThat(actualResponse).extracting("system","name","date","value")
                   .isEqualTo(Arrays.asList("system","name",DateAndTimeUtil.convertFromUnixTime(Optional.of(1499070300000l)),2));
        }
        @Test
        @DisplayName("should create metrics with default value of 1 when field value is absent")
        public void createMetricsWithoutValue(){
            var actualResponse= webTestClient.post()
                    .uri("/metrics")
                    .bodyValue(metricsModelWithOutValue)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsEntity.class).returnResult().getResponseBody();

            assertThat(actualResponse.getValue()).isEqualTo(1);
        }

        @Test
        @DisplayName("should create metrics with date set to current date when field date is absent")
        public void createMetricsWithoutDate(){
            var actualResponse= webTestClient.post()
                    .uri("/metrics")
                    .bodyValue(metricsModelWithOutDate)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsEntity.class).returnResult().getResponseBody();
            assertThat(actualResponse.getDate().toLocalDate()).isEqualTo(LocalDateTime.now().toLocalDate());
        }

        @Test
        @DisplayName("should return bad request with missing system")
        public void createMetricsMissingSystem(){
            var actualResponse= webTestClient.post()
                    .uri("/metrics")
                    .bodyValue(metricsModelWithOutSystem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class).returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
        }

        @Test
        @DisplayName("should return bad request with missing name")
        public void createMetricsMissingName(){
            var actualResponse= webTestClient.post()
                    .uri("/metrics")
                    .bodyValue(metricsModelWithOutName)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class).returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
        }

    }
    @Nested
    class UpdateMetric{
        @Test
        @DisplayName("update metrics with all fields")
        @Sql("classpath:test_data/seed_data.sql")
        public void updateMetrics(){
            var actualResponse= webTestClient.put()
                    .uri("/metrics/1")
                    .bodyValue(updateMetrics)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsEntity.class)
                    .returnResult().getResponseBody();
           assertThat(actualResponse).extracting("system").isEqualTo("system2");
        }
        @Test
        @DisplayName("update metrics with missing value should increment value by 1")
        @Sql("classpath:test_data/seed_data.sql")
        public void updateMetricsWithMissingValue(){
            var actualResponse= webTestClient.put()
                    .uri("/metrics/1")
                    .bodyValue(updateMetricsWithMissingValue)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsEntity.class)
                    .returnResult().getResponseBody();
            assertThat(actualResponse).extracting("value").isEqualTo(2);
        }

        @Test
        @DisplayName("should throw 404 when id not found")
        @Sql("classpath:test_data/seed_data.sql")
        public void updateMetricsNotFound(){
            var actualResponse= webTestClient.put()
                    .uri("/metrics/5")
                    .bodyValue(updateMetricsWithMissingValue)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .returnResult().getResponseBody();
            assertThat(actualResponse).contains("The specified metric was not found");
        }
        @Test
        @DisplayName("should throw 400 bad request when system is missing")
        @Sql("classpath:test_data/seed_data.sql")
        public void updateMetricsMissingMissingSystem(){
            var actualResponse= webTestClient.put()
                    .uri("/metrics/1")
                    .bodyValue(updateMetricsWithMissingSystem)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
        }
        @Test
        @DisplayName("should throw 400 bad request when name is missing")
        @Sql("classpath:test_data/seed_data.sql")
        public void updateMetricsMissingMissingName(){
            var actualResponse= webTestClient.put()
                    .uri("/metrics/1")
                    .bodyValue(updateMetricsWithMissingName)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
        }
        @Test
        @DisplayName("should throw 400 bad request when date is missing")
        @Sql("classpath:test_data/seed_data.sql")
        public void updateMetricsMissingMissingDate(){
            var actualResponse= webTestClient.put()
                    .uri("/metrics/1")
                    .bodyValue(updateMetricsWithMissingDate)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class)
                    .returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");
        }
    }

    @Nested
    class GetMetricsSummary{
        @Test
        @DisplayName("should return metrics summary")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void getMetricsSummary(){
            var actualResponse= webTestClient.get()
                    .uri( builder -> builder.path("/metricsummary")
                            .queryParam("system","system")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsSummary.class).returnResult().getResponseBody();
            assertThat(actualResponse.getValue()).isEqualTo(3);
            assertThat(actualResponse).extracting("system").isEqualTo("system");
        }
        @Test
        @DisplayName("should return 400 when system is missing ")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void badRequest(){
            var actualResponse= webTestClient.get()
                    .uri( builder -> builder.path("/metricsummary")
                            .queryParam("system","")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class).returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");

        }

    }
}
