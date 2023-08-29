package uk.claritygroup.kotlin.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.claritygroup.kotlin.entity.Metrics as MetricsEntity
import uk.claritygroup.kotlin.model.request.CreateMetrics
import uk.claritygroup.kotlin.model.request.UpdateMetrics
import java.time.LocalDateTime

class TransformTest {
    @Test
    fun `create persistent entity from create model`(){
        assertThat(CreateMetrics(
            system = "system",
            name = "name",
            date = 1499070300005L,
            value = 1
        ).toEntity()).extracting("system","name","value")
            .isEqualTo(listOf("system","name",1))
    }
   @Test
    fun `create persistent entity from update model`(){
     assertThat(UpdateMetrics(
         system = "system",
         name= "name",
         date= 1499070300005L,
     ).toEntity()).extracting("system","name","value")
         .isEqualTo(listOf("system","name",1))
    }
    @Test
    fun `create response model from persistent entity`(){
      assertThat(MetricsEntity(
          system = "system",
          name= "name",
          date = LocalDateTime.now(),
          value =1
      ).toResponse()).extracting("system","name","value")
          .isEqualTo(listOf("system","name",1))
    }
}