
package com.sathvik.calculator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
public class CalculatorController {

    @GetMapping("/api/v1/add")
    public Map<String, Object> add(
            @RequestParam int a,
            @RequestParam int b) {
        return response("add", a, b, a + b);
    }

    @GetMapping("/api/v1/subtract")
    public Map<String, Object> subtract(
            @RequestParam int a,
            @RequestParam int b) {
        return response("subtract", a, b, a - b);
    }

    @GetMapping("/api/v1/multiply")
    public Map<String, Object> multiply(
            @RequestParam int a,
            @RequestParam int b) {
        return response("multiply", a, b, a * b);
    }

    @GetMapping("/api/v1/divide")
    public Map<String, Object> divide(
            @RequestParam int a,
            @RequestParam int b) {
        if (b == 0) {
            throw new IllegalArgumentException(
                    "Division by zero is not allowed");
        }

        return response("divide", a, b, (double) a / b);
    }

    private Map<String, Object> response(
            String operation, int a, int b, Number result) {
        return Map.of(
                "operation", operation,
                "a", a,
                "b", b,
                "result", result
        );
    }
}

@RestControllerAdvice
class CalculatorExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Bad Request",
                        "message", exception.getMessage()
                ));
    }

    @ExceptionHandler(
            org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParameter(
            org.springframework.web.bind.MissingServletRequestParameterException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Bad Request",
                        "message", "Missing required parameter: "
                                + exception.getParameterName()
                ));
    }

    @ExceptionHandler(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleInvalidParameter(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Bad Request",
                        "message", "Invalid value for parameter: "
                                + exception.getName()
                ));
    }
}
