package com.disasteralert.service;

import org.springframework.stereotype.Service;

import com.disasteralert.model.PredictionResult;

@Service
public class PredictionService {

    public PredictionResult predict(
            double temperature,
            double humidity,
            double rainfall,
            double windSpeed,
            double waterLevel,
            double seismic) {

        // Earthquake prediction
        if (seismic >= 5.5) {
            return new PredictionResult(
                    "EARTHQUAKE",
                    "HIGH",
                    Math.min(99, seismic * 14.2),
                    92,
                    "Significant seismic activity detected.",
                    "Drop, cover and hold; move away from unsafe structures."
            );
        }

        // Flood prediction
        if (rainfall > 100 || waterLevel >= 5) {
            return new PredictionResult(
                    "FLOOD",
                    rainfall > 200 ? "HIGH" : "MEDIUM",
                    Math.min(98, 55 + rainfall / 8),
                    88,
                    "Heavy rainfall or elevated water level indicates flood risk.",
                    "Move to higher ground and follow local evacuation instructions."
            );
        }

        // Cyclone prediction
        if (windSpeed >= 55 && humidity >= 70) {
            return new PredictionResult(
                    "CYCLONE",
                    "HIGH",
                    82,
                    86,
                    "Strong winds and high humidity indicate cyclone risk.",
                    "Avoid coastal travel and monitor official warnings."
            );
        }

        // Heatwave prediction
        if (temperature > 40 && humidity < 25) {
            return new PredictionResult(
                    "HEATWAVE",
                    "HIGH",
                    91,
                    94,
                    "Extreme temperature with low humidity indicates heatwave risk.",
                    "Stay hydrated and avoid direct sun during peak hours."
            );
        }

        // Normal conditions
        return new PredictionResult(
                "NORMAL",
                "LOW",
                12,
                79,
                "Current environmental conditions are within normal demonstration limits.",
                "Continue monitoring and follow local safety guidance."
        );
    }
}