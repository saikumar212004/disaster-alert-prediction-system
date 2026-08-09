package com.disasteralert.model;

public class PredictionResult {

    private String disasterType;
    private String riskLevel;
    private double probability;
    private int confidence;
    private String message;
    private String recommendation;

    public PredictionResult(
            String disasterType,
            String riskLevel,
            double probability,
            int confidence,
            String message,
            String recommendation) {

        this.disasterType = disasterType;
        this.riskLevel = riskLevel;
        this.probability = probability;
        this.confidence = confidence;
        this.message = message;
        this.recommendation = recommendation;
    }

    public String getDisasterType() {
        return disasterType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public double getProbability() {
        return probability;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getMessage() {
        return message;
    }

    public String getRecommendation() {
        return recommendation;
    }
}