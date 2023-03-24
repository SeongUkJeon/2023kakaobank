package com.kakaobank.transactiongenerator.generator;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RecordGenerator<Record> {
    Record create(Object... args) throws JsonProcessingException;
}
