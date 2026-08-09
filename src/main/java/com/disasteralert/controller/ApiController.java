package com.disasteralert.controller;

import com.disasteralert.model.PredictionResult;
import com.disasteralert.repository.DisasterRepository;
import com.disasteralert.service.PredictionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final DisasterRepository repo;
    private final PredictionService predictor;

    public ApiController(
            DisasterRepository repo,
            PredictionService predictor) {

        this.repo = repo;
        this.predictor = predictor;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return repo.stats();
    }

    @GetMapping("/dashboard")
    public List<Map<String, Object>> dashboard() {
        return repo.dashboard();
    }

    @GetMapping("/alerts")
    public List<Map<String, Object>> alerts() {
        return repo.alerts();
    }

    @GetMapping("/history")
    public List<Map<String, Object>> history() {
        return repo.history();
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return repo.users();
    }

    @GetMapping("/credentials")
    public List<Map<String, Object>> credentials() {
        return repo.credentials();
    }

    @GetMapping("/logs")
    public List<Map<String, Object>> logs() {
        return repo.logs();
    }

    @PostMapping("/predict")
    public Map<String, Object> predict(
            @RequestBody Map<String, Object> x) {

        String location = String.valueOf(
                x.getOrDefault("location", "Demo Region")
        );

        double temperature = Double.parseDouble(
                String.valueOf(
                        x.getOrDefault("temperature", 30)
                )
        );

        double humidity = Double.parseDouble(
                String.valueOf(
                        x.getOrDefault("humidity", 60)
                )
        );

        double rainfall = Double.parseDouble(
                String.valueOf(
                        x.getOrDefault("rainfall", 20)
                )
        );

        double windSpeed = Double.parseDouble(
                String.valueOf(
                        x.getOrDefault("windSpeed", 10)
                )
        );

        double waterLevel = Double.parseDouble(
                String.valueOf(
                        x.getOrDefault("waterLevel", 2)
                )
        );

        double seismic = Double.parseDouble(
                String.valueOf(
                        x.getOrDefault("seismic", 2)
                )
        );

        // Generate prediction
        PredictionResult prediction =
                predictor.predict(
                        temperature,
                        humidity,
                        rainfall,
                        windSpeed,
                        waterLevel,
                        seismic
                );

        long readingId = repo.insertReading(
                location,
                temperature,
                humidity,
                rainfall,
                windSpeed,
                waterLevel,
                seismic,
                "WEB DEMO"
        );

        repo.insertPrediction(
                readingId,
                prediction
        );

        return Map.of(
                "readingId",
                readingId,
                "prediction",
                prediction
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> x) {

        String email = x.getOrDefault("email", "");
        String password = x.getOrDefault("password", "");

        if (email.equals("admin@disaster.local")
                && password.equals("admin123")) {

            return Map.of(
                    "success", true,
                    "name", "System Administrator",
                    "role", "ADMIN"
            );
        }

        if (email.equals("authority@disaster.local")
                && password.equals("authority123")) {

            return Map.of(
                    "success", true,
                    "name", "Emergency Authority",
                    "role", "AUTHORITY"
            );
        }

        if (email.equals("user@disaster.local")
                && password.equals("user123")) {

            return Map.of(
                    "success", true,
                    "name", "Demo Citizen",
                    "role", "USER"
            );
        }

        return Map.of(
                "success", false,
                "message", "Invalid demo credentials"
        );
    }

    // Credential verification
    @GetMapping("/credential/verify/{code}")
    public Map<String, Object> verify(
            @PathVariable String code) {

        List<Map<String, Object>> rows =
                repo.credentials()
                        .stream()
                        .filter(x ->
                                code.equals(
                                        String.valueOf(
                                                x.get("credential_code")
                                        )
                                )
                        )
                        .toList();

        if (rows.isEmpty()) {

            return Map.of(
                    "valid", false,
                    "message", "Credential not found"
            );
        }

        Map<String, Object> credential = rows.get(0);

        boolean valid =
                "VALID".equals(
                        String.valueOf(
                                credential.get("status")
                        )
                );

        return Map.of(
                "valid", valid,
                "credential", credential
        );
    }
}