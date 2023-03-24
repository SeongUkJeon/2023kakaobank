package com.kakaobank.evaluator.global;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface RecordEvaluator<Record> {
    Record create(Map<String, String> fields) throws JsonProcessingException;
}