package uk.claritygroup.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import uk.claritygroup.entity.MetricsEntity;
import uk.claritygroup.entity.MetricsEntity_;

import java.time.LocalDateTime;
import java.util.Optional;

public class MetricsSpecifications {

    public static Specification<MetricsEntity> systemEquals(final String system ){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(MetricsEntity_.SYSTEM),system);
    }
    public static Specification<MetricsEntity> nameEquals(final Optional<String> name ){
        return (root, query, criteriaBuilder) ->
                name.isPresent()?criteriaBuilder.equal(root.get(MetricsEntity_.NAME),name.get()): criteriaBuilder.conjunction();
    }
    public static Specification<MetricsEntity> dateIsGreaterThanEqual(final Optional<LocalDateTime> date ){
        return (root, query, criteriaBuilder) ->
                date.isPresent()?criteriaBuilder.greaterThanOrEqualTo(root.get(MetricsEntity_.DATE),date.get()): criteriaBuilder.conjunction();
    }

    public static Specification<MetricsEntity> dateIsLessThanEqual( final Optional<LocalDateTime> date){
        return (root, query, criteriaBuilder) ->
                date.isPresent()?criteriaBuilder.lessThanOrEqualTo(root.get(MetricsEntity_.DATE),date.get()): criteriaBuilder.conjunction();
    }

}
