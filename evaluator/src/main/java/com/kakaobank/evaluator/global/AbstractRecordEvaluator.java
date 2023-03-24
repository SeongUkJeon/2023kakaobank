package com.kakaobank.evaluator.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractRecordEvaluator implements RecordEvaluator<Map.Entry<String, String>>, WithLogger {
    private static final ObjectMapper mapper = new ObjectMapper();

    protected abstract FraudDetection saveFields(Map<String, String> fields) throws SQLException, ParseException;

    @Override
    public Map.Entry<String, String> create(Map<String, String> fields) throws JsonProcessingException {
        logger.info("AbstractRecordEvaluator created.");

        FraudDetection fraudDetection = null;
        try {
            fraudDetection = saveFields(fields);
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        if(fraudDetection != null) {
            try {
                logger.info("!!!Abnormal log: {}", fraudDetection);
                String value = mapper.writeValueAsString(fraudDetection);
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
            } catch (JsonProcessingException e) {
                logger.error("Fail to convert {} to json.", fraudDetection);
                throw e;
            }
        }
        return null;
    }
}
