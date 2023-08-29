package uk.claritygroup.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.claritygroup.kotlin.respository.MetricsQuery
import uk.claritygroup.kotlin.respository.toSpec

class SpecificationsTest {
@Test
  fun `should return jpa specification for a given metrics query`(){
    var metricsQuerySpec= MetricsQuery(
        system = "system",
        name= "name",
        fromDate = 12223L,
        toDate = 13456L
    ).toSpec()
    assertThat(metricsQuerySpec).isNotNull
   }
}