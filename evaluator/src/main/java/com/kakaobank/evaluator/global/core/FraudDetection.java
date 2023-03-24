package com.kakaobank.evaluator.global.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.evaluator.global.Message;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FraudDetection {
    @JsonProperty("results") private List<Message> results;

    public FraudDetection(List<Message> results) {
        this.results = results;
    }

    public List<Message> getResults() {
        return results;
    }
}
