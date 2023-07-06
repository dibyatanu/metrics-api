package uk.claritygroup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name="metrics", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"system","name","date"})
})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MetricsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String system;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDateTime date;
    @Column(nullable = false)
    private Integer value;
}
