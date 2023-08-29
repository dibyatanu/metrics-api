package uk.claritygroup.kotlin.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.claritygroup.kotlin.utils.convertToLocalDateAndTime
import uk.claritygroup.kotlin.utils.convertToUnixTimeStamp
import java.time.LocalDate
import java.time.LocalDateTime


class DateAndTimeTest {

    @Nested
    inner class ConvertToLocalDateAndTime {
        @Test
        fun `should convert unix time stamp to LocalDateTime`() {
            val unix = 1499070300005L
            assertThat(unix.convertToLocalDateAndTime()?.toLocalDate()).isEqualTo(LocalDate.of(2017, 7, 3))
        }

        @Test
        fun `should return null if unix time stamp is null`() {
            val n = null
            assertThat(n.convertToLocalDateAndTime()).isNull()
        }
    }
    @Nested
    inner class ConvertToUnixTimeStamp {
        @Test
        fun `should convert LocalDateAndTime to unix format`() {
            assertThat(LocalDateTime.of(2017, 1, 1, 1, 1, 1).convertToUnixTimeStamp())
                .isEqualTo(1483232461000L)
        }
    }
}