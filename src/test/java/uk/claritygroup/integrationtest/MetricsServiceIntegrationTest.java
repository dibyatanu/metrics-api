package uk.claritygroup.integrationtest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.claritygroup.BaseTest;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.model.CreateMetrics;
import uk.claritygroup.model.MetricsSummary;


import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@ActiveProfiles("test")
@Sql( "classpath:test_data/clear_data.sql")
public class MetricsServiceIntegrationTest extends BaseTest {
    @Autowired
    private WebTestClient webTestClient;
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
         assertThat(actualResponse.getSystem()).isEqualTo("system");
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
    class GetAllMetricsFilteredByQueryParam{
        @Test
        @DisplayName("should return list of metrics")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void returnMetricsByCriteria(){
            var actualResponse=webTestClient.get()
                    .uri("/metrics?system=system")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(MetricsEntity.class).returnResult().getResponseBody();
            assertThat(actualResponse.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return 400 bad request")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void badRequest(){
            var actualResponse=webTestClient.get()
                    .uri("/metrics?system=")
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

           assertThat(actualResponse.getSystem()).isEqualTo("system");
        }
        @Test
        @DisplayName("should create metrics without value fields")
        public void createMetricsMinimalField(){
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
        @DisplayName("should return bad request with missing system")
        public void createMetricsMissingSystem(){
            var actualResponse= webTestClient.post()
                    .uri("/metrics")
                    .bodyValue(invalidRequestBody)
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
           assertThat(actualResponse.getSystem()).isEqualTo("system2");
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
            assertThat(actualResponse.getValue()).isEqualTo(2);
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
                    .bodyValue(invalidUpdateRequest)
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
                    .uri("/metricsummary?system=system")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(MetricsSummary.class).returnResult().getResponseBody();
            assertThat(actualResponse.getValue()).isEqualTo(3);
        }
        @Test
        @DisplayName("should return 400 when system is missing ")
        @Sql("classpath:test_data/multi_seed_data.sql")
        public void badRequest(){
            var actualResponse= webTestClient.get()
                    .uri("/metricsummary?system=")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(String.class).returnResult().getResponseBody();
            assertThat(actualResponse).contains("A required parameter was not supplied or is invalid");

        }

    }
}
