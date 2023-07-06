package uk.claritygroup.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricsSummary {
    private final String system;
    private final String name;
    private final Long from;
    private final Long to;
    private final Integer value;
}
