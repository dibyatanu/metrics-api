package uk.claritygroup.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateMetrics {
    @NotNull
    private final String system;
    @NotNull
    private final String name;
    private final Long date;
    private final Integer value;

    public CreateMetrics(final String system, final String name, final Long date, final Integer value){
        this.system=system;
        this.name=name;
        this.date= Optional.ofNullable(date).isPresent()?Optional.of(date).get() :System.currentTimeMillis() ;
        this.value=Optional.ofNullable(value).isPresent()?Optional.of(value).get() :1;
    }
}
