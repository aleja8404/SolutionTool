package com.elingenio.Proyecto.Services.dto;

public class PredictionCustomer {

private final String prediction;
private final String confidence;

public PredictionCustomer(String prediction, String confidence) {
    this.prediction = prediction;
    this.confidence = confidence;
}

public String getPrediction() {
    return prediction;
}

public String getConfidence() {
    return confidence;
}
}