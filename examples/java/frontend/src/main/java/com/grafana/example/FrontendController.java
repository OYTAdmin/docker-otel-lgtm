package com.grafana.example;

import io.opentelemetry.api.trace.Span;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
public class FrontendController {

    private final RestTemplate checkOutRestTemplate = new RestTemplate();

    @GetMapping("/shop")
    public String index(@RequestParam("customer") Optional<String> customerId) throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Customer-ID", customerId.orElse("anonymous"));
        ResponseEntity<String> response = checkOutRestTemplate.exchange("http://localhost:8082/checkout",
                HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        for (String timing : response.getHeaders().get("Server-Timing")) {
            if (timing.endsWith("-01\"")) {
                // wor
                // sampled traces are marked with a server timing header
                Span.current().setAttribute("sampled", true);
            }
        }
        return response.getBody();
    }
}