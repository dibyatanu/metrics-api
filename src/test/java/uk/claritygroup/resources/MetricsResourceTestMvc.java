package uk.claritygroup.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.claritygroup.service.MetricsService;

//@WebMvcTest(controllers = MerticsResource.class)
//@ContextConfiguration(classes = MerticsResource.class)
//@SpringBootTest
public class MetricsResourceTestMvc {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MetricsService mockMetricsService;


   @Nested
    class GetMetricsList{
       @Test
        @DisplayName("Should get list of metrics by filter criteria")
        public void getMetricsList(){

        }

    }
}
