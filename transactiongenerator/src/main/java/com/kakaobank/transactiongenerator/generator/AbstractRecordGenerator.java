package com.kakaobank.transactiongenerator.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.global.WithLogger;

import java.util.Map;
import java.util.UUID;

public abstract class AbstractRecordGenerator implements RecordGenerator<Map.Entry<String, String>>, WithLogger {
    private final ObjectMapper mapper = new ObjectMapper();

    protected abstract Sample kafkaMessageGen(Object... args);

    @Override
    public Map.Entry<String, String> create(Object... args) throws JsonProcessingException {
        logger.info("AbstractRecordGenerator created.");

        Sample message = kafkaMessageGen(args);
        try {
            logger.info("Message: {}", message);
            String value = mapper.writeValueAsString(message);
            return new Map.Entry<String, String>() {
                @Override
                public String getKey() {
                    return UUID.randomUUID().toString();
                }
                @Override
                public String getValue() {
                    return value;
                }
                @Override
                public String setValue(String value) {
                    throw new UnsupportedOperationException();
                }
            };
        } catch (JsonProcessingException e)  {
            logger.error("Fail to convert {} to json.", message);
            throw e;
        }
    }
}
