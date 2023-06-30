package uk.claritygroup.utility;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class DateAndTimeUtilTest {
    @Test
    public void shouldConvertUnixTimeToLocalDateTime() {
        assertThat(DateAndTimeUtil.convertFromUnixTime(Optional.ofNullable(1499070300005l)).toLocalDate())
                .isEqualTo(LocalDate.of(2017, 07, 03));
    }
    @Test
    public void shouldConvertLocalDateTimeToUnix(){
        assertThat(DateAndTimeUtil.convertToUnixTime(LocalDateTime.of(2017,1,1,1,1,1)))
                .isEqualTo(1483232461000L);
    }
}
