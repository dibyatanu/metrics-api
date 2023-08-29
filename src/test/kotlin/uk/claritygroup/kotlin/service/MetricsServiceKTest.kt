package uk.claritygroup.kotlin.service

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.jpa.domain.Specification
import uk.claritygroup.kotlin.exception.BadRequestException
import uk.claritygroup.kotlin.model.response.MetricsSummary
import uk.claritygroup.kotlin.model.request.CreateMetrics as CreateMetricsRequest
import uk.claritygroup.kotlin.model.request.UpdateMetrics as UpdateMetricsRequest
import uk.claritygroup.kotlin.entity.Metrics as MetricsEntity
import uk.claritygroup.kotlin.model.response.Metrics as MetricsResponse
import uk.claritygroup.kotlin.respository.MetricsRepositoryKotlin
import java.time.LocalDateTime
import java.util.*
@ExtendWith(MockKExtension::class)
class MetricsServiceKTest {
    @MockK
    private lateinit var mockMetricsRepository:MetricsRepositoryKotlin
    private lateinit var metricsServiceK: MetricsServiceK
    @BeforeEach
    fun init(){
        clearMocks(mockMetricsRepository)
        metricsServiceK= MetricsServiceK(mockMetricsRepository)
    }

    @Nested
    inner class GetMetricsListByFilter{
        @Test
        fun `Should return list of Metrics`(){
            val argumentCaptor = slot<Specification<MetricsEntity>>()
            every { mockMetricsRepository.findAll(capture(argumentCaptor))} returns listOfMetrics
            val expectedListOfMetrics= metricsServiceK.getMetricsListByFilter("system","name",1499070300000L,1499070300000L)
            verify (exactly =1  ){mockMetricsRepository.findAll(argumentCaptor.captured)}
            assertThat(expectedListOfMetrics).containsAnyOf(metricsResponse)
            assertThat(expectedListOfMetrics).hasOnlyElementsOfType(MetricsResponse::class.java)
        }
        @Test
        fun `Should throw Entity not found exception`() {
            val argumentCaptor = slot<Specification<MetricsEntity>>()
            every { mockMetricsRepository.findAll(capture(argumentCaptor))} returns emptyList()
            val exception=assertThrows<EntityNotFoundException>{
                metricsServiceK.getMetricsListByFilter("system2","name",1499070300000L,1499070300000L)
            }
            assertThat(exception).isExactlyInstanceOf(EntityNotFoundException::class.java)
            assertThat(exception.message).isEqualTo("No metrics found matching the criteria")
            verify (exactly =1  ){mockMetricsRepository.findAll(argumentCaptor.captured)}
        }
    }
    @Nested
    inner class GetMetricsById{
        @Test
        fun `Should throw Entity not found exception`() {
            val argumentCaptor = slot<Long>()
            every { mockMetricsRepository.findById(capture(argumentCaptor)) } returns Optional.empty()
            val exception = assertThrows<EntityNotFoundException> {
                metricsServiceK.getMetricsById(1)
            }
            assertThat(exception).isExactlyInstanceOf(EntityNotFoundException::class.java)
            assertThat(exception.message).isEqualTo("The specified metric was not found")
            verify(exactly = 1) { mockMetricsRepository.findById(argumentCaptor.captured) }
            assertThat(argumentCaptor.captured).isEqualTo(1)
        }

        @Test
        fun `Should return metrics response`(){
            val argumentCaptor = slot<Long>()
            every { mockMetricsRepository.findById(capture(argumentCaptor)) } returns Optional.of(
                metricsEntityWithValue1
            )
            val actualMetricsResponse= metricsServiceK.getMetricsById(1)
            verify(exactly = 1) { mockMetricsRepository.findById(argumentCaptor.captured)}
            assertThat(argumentCaptor.captured).isEqualTo(1)
            assertThat(actualMetricsResponse).extracting("system","name","value")
                .isEqualTo(listOf("system","name",1))
            assertThat(actualMetricsResponse).isExactlyInstanceOf(MetricsResponse::class.java)
        }
    }
    @Nested
    inner class CreateMetrics{
        @Test
        fun `Should create metrics`(){
            val argumentCaptor = slot<MetricsEntity>()
            every { mockMetricsRepository.save(capture(argumentCaptor)) } returns metricsEntityWithValue1
            val actualMetricsResponse=metricsServiceK.createMetrics(createMetricsRequest)
            verify ( exactly =1 ) {mockMetricsRepository.save(argumentCaptor.captured)}
            assertThat(argumentCaptor.captured).isExactlyInstanceOf(MetricsEntity::class.java)
            assertThat(argumentCaptor.captured).extracting("system","name","value")
                .isEqualTo(listOf("system","name",1))
            assertThat(actualMetricsResponse).isExactlyInstanceOf(MetricsResponse::class.java)
            assertThat(actualMetricsResponse).extracting("system","name","value")
                .isEqualTo(listOf("system","name",1))
        }
    }

    @Nested
    inner class UpdateMetrics{
        @Test
        fun `should throw entity not found exception`(){
            val argumentCaptor = slot<Long>()
            every { mockMetricsRepository.findById(capture(argumentCaptor)) } returns Optional.empty()
            val exception=assertThrows<EntityNotFoundException> { metricsServiceK.updateMetrics(1,
                updateMetricsRequestUnMatchingName
            ) }
            assertThat(exception).isExactlyInstanceOf(EntityNotFoundException::class.java)
            assertThat(exception.message).isEqualTo("The specified metric was not found")
            verify ( exactly =1 ) {mockMetricsRepository.findById(argumentCaptor.captured)}
            assertThat(argumentCaptor.captured).isEqualTo(1)
        }
        @Test
        fun `Should throw bad request exception when update system does not match the entity`(){
            val argumentCaptor = slot<Long>()
            every { mockMetricsRepository.findById(capture(argumentCaptor)) } returns Optional.of(metricsEntityWithName2)
            val exception=assertThrows<BadRequestException> {  metricsServiceK.updateMetrics(1,
                updateMetricsRequestUnMatchingSystem
            ) }
            assertThat(exception).isExactlyInstanceOf(BadRequestException::class.java)
            assertThat(exception.message).isEqualTo("A required system or name or date does not match the existing metric")
            verify ( exactly =1 ) {mockMetricsRepository.findById(argumentCaptor.captured)}
            assertThat(argumentCaptor.captured).isEqualTo(1)
            verify (exactly = 0){mockMetricsRepository.save(any<MetricsEntity>())  }
        }
        @Test
        fun `Should throw bad request exception when update name does not match the entity`(){
            val argumentCaptor = slot<Long>()
            every { mockMetricsRepository.findById(capture(argumentCaptor)) } returns Optional.of(metricsEntityWithName2)
            val exception=assertThrows<BadRequestException> {  metricsServiceK.updateMetrics(1,
                updateMetricsRequestUnMatchingName
            ) }
            assertThat(exception).isExactlyInstanceOf(BadRequestException::class.java)
            assertThat(exception.message).isEqualTo("A required system or name or date does not match the existing metric")
            verify ( exactly =1 ) {mockMetricsRepository.findById(argumentCaptor.captured)}
            assertThat(argumentCaptor.captured).isEqualTo(1)
            verify (exactly = 0){mockMetricsRepository.save(any<MetricsEntity>())  }
        }
        @Test
        fun `Should throw bad request exception when update date does not match the entity`(){
            val argumentCaptor = slot<Long>()
            every { mockMetricsRepository.findById(capture(argumentCaptor)) } returns Optional.of(metricsEntityWithName2)
            val exception=assertThrows<BadRequestException> {  metricsServiceK.updateMetrics(1,
                updateMetricsRequestUnMatchingDate
            ) }
            assertThat(exception).isExactlyInstanceOf(BadRequestException::class.java)
            assertThat(exception.message).isEqualTo("A required system or name or date does not match the existing metric")
            verify ( exactly =1 ) {mockMetricsRepository.findById(argumentCaptor.captured)}
            assertThat(argumentCaptor.captured).isEqualTo(1)
            verify (exactly = 0){mockMetricsRepository.save(any<MetricsEntity>())  }
        }
        @Test
        fun `Update metrics request with missing value should increment existing value by 1`(){
            val argumentCaptorId= slot<Long>()
            val argumentCaptorMetricsEntity= slot<MetricsEntity>()
            every{mockMetricsRepository.findById(capture(argumentCaptorId))} returns Optional.of(metricsEntityWithName2)
            every { mockMetricsRepository.save(capture(argumentCaptorMetricsEntity))} returns metricsEntityWithName2.copy(value= metricsEntityWithName2.value +1)
            val actualUpdatedMetricsEntity = metricsServiceK.updateMetrics(1, validUpdateMetricsRequestWithMissingValue)
            verify ( exactly =1 ) {mockMetricsRepository.findById(argumentCaptorId.captured)}
            assertThat(argumentCaptorId.captured).isEqualTo(1)
             verify (exactly = 1){mockMetricsRepository.save(capture(argumentCaptorMetricsEntity))}
             assertThat(argumentCaptorMetricsEntity.captured).extracting("value").isEqualTo(2)
            assertThat(actualUpdatedMetricsEntity).extracting("value").isEqualTo(2)
        }

        @Test
        fun `Update metrics request update existing value`(){
            val argumentCaptorId= slot<Long>()
            val argumentCaptorMetricsEntity= slot<MetricsEntity>()
            every{mockMetricsRepository.findById(capture(argumentCaptorId))} returns Optional.of(metricsEntityWithName2)
            every { mockMetricsRepository.save(capture(argumentCaptorMetricsEntity))} returns metricsEntityWithName2
                    .copy(value= metricsEntityWithName2.value + validUpdateMetricsRequest.value!!)
            val actualUpdatedMetricsEntity = metricsServiceK.updateMetrics(1, validUpdateMetricsRequest)
            verify ( exactly =1 ) {mockMetricsRepository.findById(argumentCaptorId.captured)}
            assertThat(argumentCaptorId.captured).isEqualTo(1)
            verify (exactly = 1){mockMetricsRepository.save(capture(argumentCaptorMetricsEntity))}
            assertThat(argumentCaptorMetricsEntity.captured).extracting("value").isEqualTo(6)
            assertThat(actualUpdatedMetricsEntity).extracting("value").isEqualTo(6)
        }
    }
    @Nested
    inner class GetMetricsSummary{
        @Test
        fun `should return metrics summary wit all param`(){
            val argumentCaptor = slot<Specification<MetricsEntity>>()
            every { mockMetricsRepository.findAll(capture(argumentCaptor))} returns listOfMetrics
            val expectedMetricsSummary= metricsServiceK.getMetricsSummary("system","name",1499070300000L,1499070300000L)
            verify (exactly =1  ){mockMetricsRepository.findAll(argumentCaptor.captured)}
            assertThat(expectedMetricsSummary).isExactlyInstanceOf(MetricsSummary::class.java)
            assertThat(expectedMetricsSummary).extracting("value").isEqualTo(6)
        }
    }


     companion object{
        val metricsResponse= MetricsResponse(
            system = "system",
            name= "name",
            date= -62135593065000,
            value= 1
        )
        val metricsEntityWithValue1= MetricsEntity(
            system = "system",
            name= "name",
            date= LocalDateTime.of(1, 1, 1, 1, 1),
            value = 1
        )
        val metricsEntityWithValue2= MetricsEntity(
            system = "system",
            name= "name",
            date= LocalDateTime.of(1, 1, 2, 1, 1),
            value = 2
        )
        val metricsEntityWithValue3= MetricsEntity(
            system = "system",
            name= "name",
            date= LocalDateTime.of(1, 1, 3, 1, 1),
            value = 3
        )

        val metricsEntityWithName2= MetricsEntity(
            system = "system",
            name= "name2",
            date= LocalDateTime.of(1, 1, 1, 1, 1),
            value = 1
        )
        val createMetricsRequest = CreateMetricsRequest(
            system = "system",
            name= "name",
            date= -62135593065000,
            value= 1
        )
        val updateMetricsRequestUnMatchingName = UpdateMetricsRequest(
            system= "system",
            name= "name",
            date = -62135593065000,
            value= 1
        )
        val updateMetricsRequestUnMatchingSystem = UpdateMetricsRequest(
            system= "system2",
            name= "name2",
            date = -62135593065000,
            value= 1
        )
        val updateMetricsRequestUnMatchingDate = UpdateMetricsRequest(
            system= "system",
            name= "name2",
            date = -62135593065001,
            value= 1
        )
        val validUpdateMetricsRequestWithMissingValue = UpdateMetricsRequest(
            system= "system",
            name= "name2",
            date = -62135593065000
        )

        val validUpdateMetricsRequest = UpdateMetricsRequest(
            system= "system",
            name= "name2",
            date = -62135593065000,
            value= 5
        )
        val listOfMetrics= listOf(metricsEntityWithValue1, metricsEntityWithValue2, metricsEntityWithValue3)

    }
}