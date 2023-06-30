package uk.claritygroup.utility;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public final class DateAndTimeUtil {
    public static LocalDateTime convertFromUnixTime(final Optional<Long> unixTime){
        return unixTime.isPresent()?LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTime.get()), ZoneId.systemDefault()):null;
    }
    public static Long convertToUnixTime(final LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
