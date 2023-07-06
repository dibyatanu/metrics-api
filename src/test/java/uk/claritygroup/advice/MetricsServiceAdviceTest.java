package uk.claritygroup.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.claritygroup.exception.BadRequestException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricsServiceAdviceTest {

    private MetricsServiceAdvice metricsServiceAdvice;
    @BeforeEach
    public void setUp(){
        metricsServiceAdvice= new MetricsServiceAdvice();
    }
    @Test
    public void notFoundShouldReturnNotFound(){
       var actualResponse= metricsServiceAdvice.notFound(new RuntimeException("The specified metric was not found"));
       assertThat(actualResponse).extracting(ResponseEntity::getStatusCode,ResponseEntity::getBody)
               .isEqualTo(Arrays.asList(HttpStatus.NOT_FOUND,"{\"message\":\"The specified metric was not found\"}"));
    }
    @Test
    public void invalidRequestShouldReturnBadRequest(){
        var actualResponse= metricsServiceAdvice.invalidRequest(new RuntimeException("A required parameter was not supplied or is invalid"));
        assertThat(actualResponse).extracting(ResponseEntity::getStatusCode,ResponseEntity::getBody)
                .isEqualTo(Arrays.asList(HttpStatus.BAD_REQUEST,"{\"message\":\"A required parameter was not supplied or is invalid\"}"));
    }

    @Test
    public void invalidUpdateRequestShouldReturnBadRequest(){
        var actualResponse= metricsServiceAdvice.invalidUpdateRequest(new BadRequestException("A required parameter was not supplied or is invalid" +
                ", or system or name does not match the existing metric"));
        assertThat(actualResponse).extracting(ResponseEntity::getStatusCode,ResponseEntity::getBody)
                .isEqualTo(Arrays.asList(HttpStatus.BAD_REQUEST,
                        "{\"message\":\"A required parameter was not supplied or is invalid, or system or name does not match the existing metric\"}"));
    }
}
