package uk.claritygroup.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Optional;
@Getter
public class UpdateMetrics {
    @NotNull
    private final String system;
    @NotNull
    private final String name;
    @NotNull
    private final Long date;
    private final Integer value;

    public UpdateMetrics(final String system, final String name, final Long date, final Integer value){
        this.system=system;
        this.name=name;
        this.date= date;
        this.value=Optional.ofNullable(value).isPresent()?Optional.of(value).get() :1;
    }
}
