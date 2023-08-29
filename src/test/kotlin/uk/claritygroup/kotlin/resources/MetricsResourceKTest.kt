package uk.claritygroup.kotlin.resources

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.util.UriBuilder
import uk.claritygroup.BaseTest
import uk.claritygroup.kotlin.model.response.MetricsSummary
import uk.claritygroup.kotlin.service.MetricsServiceK
import uk.claritygroup.kotlin.service.MetricsServiceKTest
import uk.claritygroup.kotlin.model.request.CreateMetrics as MetricsCreateRequest
import uk.claritygroup.kotlin.model.request.UpdateMetrics as MetricsUpdateRequest
@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [MetricsResourceK::class])
@Import(MetricsResourceK::class)
@ActiveProfiles("test")
class MetricsResourceKTest: BaseTest() {
    @Autowired
   private lateinit var webTestClient: WebTestClient
    @Autowired
    private lateinit var mapper: ObjectMapper
    @MockBean
    private  lateinit var mockMetricsServiceK : MetricsServiceK

    @Nested
    inner class GetListOfMetrics{
        @Test
        fun `should return list of metrics for a given query param`(){
          whenever(mockMetricsServiceK.getMetricsListByFilter(any()
                ,any(),any(),any())).thenReturn(listOf(MetricsServiceKTest.metricsResponse))
          webTestClient.get()
                .uri { builder: UriBuilder ->
                    builder.path("/kotlin/metrics")
                        .queryParam("system", "system")
                        .queryParam("name", "name2")
                        .queryParam("from", 1223L)
                        .queryParam("to", 12234L)
                        .build()
                }
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
            val systemCaptor= argumentCaptor<String>()
            val nameCaptor = argumentCaptor<String>()
            val fromDateCaptor= argumentCaptor<Long>()
            val toDateCaptor = argumentCaptor<Long>()
            verify (mockMetricsServiceK,times(1)).getMetricsListByFilter(systemCaptor.capture()
                  ,nameCaptor.capture(),fromDateCaptor.capture(),toDateCaptor.capture())
            assertThat(systemCaptor.firstValue).isEqualTo("system")
            assertThat(nameCaptor.firstValue).isEqualTo("name2")
            assertThat(fromDateCaptor.firstValue).isEqualTo(1223L)
            assertThat(toDateCaptor.firstValue).isEqualTo(12234L)
        }

        @Test
        fun `should bad request when system value is missing`(){
           val actualErrorResponse = webTestClient.get()
                .uri { builder: UriBuilder ->
                    builder.path("/kotlin/metrics")
                        .queryParam("system", "")
                        .queryParam("name", "name2")
                        .queryParam("from", 1223L)
                        .queryParam("to", 12234L)
                        .build()
                }
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                . expectBody(String::class.java).returnResult().responseBody

           assertThat(actualErrorResponse).contains("A required parameter was not supplied or is invalid")
            verify (mockMetricsServiceK,times(0)).getMetricsListByFilter(any()
                ,any(),any(),any())
        }

    }

    @Nested
    inner class GetMetrics{
        @Test
        fun `should return metrics by Id`(){
            whenever(mockMetricsServiceK.getMetricsById(any())).thenReturn(MetricsServiceKTest.metricsResponse)
            webTestClient.get()
                .uri("/kotlin/metrics/1")
                . accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
           verify(mockMetricsServiceK).getMetricsById(1L)

        }
        @Test
        fun `should throw exception when metrics not found`(){
            whenever(mockMetricsServiceK.getMetricsById(any())).thenThrow(EntityNotFoundException("The specified metric was not found"))
           val actualErrorResponse= webTestClient.get()
                .uri("/kotlin/metrics/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(String::class.java).returnResult().responseBody
            assertThat(actualErrorResponse).contains("The specified metric was not found")
            verify(mockMetricsServiceK).getMetricsById(1L)
        }
    }
    @Nested
    inner class CreateMetrics{
      @Test
        fun `should create a metrics`(){
         webTestClient.post()
               .uri("/kotlin/metrics")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .body(BodyInserters.fromValue(mapper.writeValueAsString(metricsModelWithAllFields)))
               .exchange()
               .expectStatus().isOk
          val requestCaptor= argumentCaptor<MetricsCreateRequest>()
          verify(mockMetricsServiceK).createMetrics(requestCaptor.capture())
         assertThat(requestCaptor.firstValue).extracting("system","name","date","value")
             .isEqualTo(listOf("system","name",1499070300000L,2))
        }
        @Test
        fun `should throw exception if system value is missing`(){
            val actualErrorResponse= webTestClient.post()
                .uri("/kotlin/metrics")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapper.writeValueAsString(metricsModelWithOutSystem)))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(String::class.java).returnResult().responseBody
            assertThat(actualErrorResponse).contains("A required parameter was not supplied or is invalid")
            verify(mockMetricsServiceK, times(0)).createMetrics(any())
        }
    }
    @Nested
    inner class UpdateMetrics{
        @Test
        fun `should be able to update existing metrics`(){
            webTestClient.put()
                .uri("/kotlin/metrics/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapper.writeValueAsString(updateMetrics)))
                .exchange()
                .expectStatus().isOk
            val updateMetricsCaptor= argumentCaptor<MetricsUpdateRequest>()
            val iDCaptor= argumentCaptor<Long>()
            verify(mockMetricsServiceK).updateMetrics(iDCaptor.capture(),updateMetricsCaptor.capture())
            assertThat(updateMetricsCaptor.firstValue).extracting("system","name","date","value")
                .isEqualTo(listOf("system","name",1499070300000L,2))
            assertThat(iDCaptor.firstValue).isEqualTo(1)
        }

        @Test
        fun `should throw metrics not found`(){
            whenever(mockMetricsServiceK.updateMetrics(any(),any()))
                .thenThrow(EntityNotFoundException("The specified metric was not found"))
            val actualErrorResponse= webTestClient.put()
                .uri("/kotlin/metrics/2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapper.writeValueAsString(updateMetrics)))
                .exchange()
                .expectStatus().isNotFound
                .expectBody(String::class.java).returnResult().responseBody
            verify(mockMetricsServiceK).updateMetrics(any(),any())
            assertThat(actualErrorResponse).contains("The specified metric was not found")
        }

        @Test
        fun `should throw exception if system value is missing`(){
            webTestClient.put()
                .uri("/kotlin/metrics/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mapper.writeValueAsString(updateMetricsWithMissingSystem)))
                .exchange()
                .expectStatus().isBadRequest
            verify(mockMetricsServiceK, times(0)).updateMetrics(any(),any())

        }
    }
    @Nested
    inner class GetMetricsSummary{
        @Test
        fun `should return summary of metrics`(){
          whenever(mockMetricsServiceK.getMetricsSummary(any()
              ,any(),any(),any())).thenReturn(metricsSummary)
            webTestClient.get()
                .uri { builder: UriBuilder ->
                    builder.path("/kotlin/metricsummary")
                        .queryParam("system", "system")
                        .queryParam("name", "name2")
                        .queryParam("from", 1223L)
                        .queryParam("to", 12234L)
                        .build()
                }
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
            val systemCaptor= argumentCaptor<String>()
            val nameCaptor = argumentCaptor<String>()
            val fromDateCaptor= argumentCaptor<Long>()
            val toDateCaptor = argumentCaptor<Long>()
            verify(mockMetricsServiceK).getMetricsSummary(
                systemCaptor.capture(),nameCaptor.capture(), fromDateCaptor.capture(),toDateCaptor.capture())
            assertThat(systemCaptor.firstValue).isEqualTo("system")
            assertThat(nameCaptor.firstValue).isEqualTo("name2")
            assertThat(fromDateCaptor.firstValue).isEqualTo(1223L)
            assertThat(toDateCaptor.firstValue).isEqualTo(12234L)
      }
        @Test
        fun `should throw return bad request when system value is missing`(){
            webTestClient.get()
                .uri { builder: UriBuilder ->
                    builder.path("/kotlin/metricsummary")
                        .queryParam("system", "")
                        .queryParam("name", "name2")
                        .queryParam("from", 1223L)
                        .queryParam("to", 12234L)
                        .build()
                }
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
            verify(mockMetricsServiceK, times(0)).getMetricsSummary(
                any(),any(),any(),any())
        }
    }

    companion object{
        val metricsSummary = MetricsSummary(
            system = "system",
            name = "name",
            from = 12345L,
            to = 123456L,
            value = 1

        )
    }
}
