
package com.sathvik.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testAdditionAPI() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                .param("a", "2")
                .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(2))
                .andExpect(jsonPath("$.b").value(3))
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void testSubtractionAPI() throws Exception {
        mockMvc.perform(get("/api/v1/subtract")
                .param("a", "8")
                .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    void testMultiplicationAPI() throws Exception {
        mockMvc.perform(get("/api/v1/multiply")
                .param("a", "4")
                .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("multiply"))
                .andExpect(jsonPath("$.result").value(12));
    }

    @Test
    void testDivisionAPI() throws Exception {
        mockMvc.perform(get("/api/v1/divide")
                .param("a", "10")
                .param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    void testDivisionByZero() throws Exception {
        mockMvc.perform(get("/api/v1/divide")
                .param("a", "10")
                .param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Division by zero is not allowed"));
    }

    @Test
    void testMissingParameter() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                .param("a", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testInvalidParameter() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                .param("a", "abc")
                .param("b", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
