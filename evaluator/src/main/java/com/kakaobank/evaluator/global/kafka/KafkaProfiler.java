package com.kakaobank.evaluator.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kakaobank.evaluator.global.RecordEvaluator;
import com.kakaobank.evaluator.global.Sender;
import com.kakaobank.evaluator.global.WithLogger;

import java.sql.Connection;
import java.util.Map;

public class KafkaProfiler implements WithLogger {
    private static final Sender<Map.Entry<String, String>> sender = new KafkaSender<>();
    RecordEvaluator<Map.Entry<String, String>> recordEvaluator;

    public void setup(String evaluator, Connection connection) throws Exception {
        recordEvaluator = (RecordEvaluator<Map.Entry<String, String>>) Class.forName(evaluator).getDeclaredConstructor(Connection.class).newInstance(connection);
        logger.info("Sender: {}, Generator: {}", sender, recordEvaluator);
    }

    public void run(Map<String, String> fields) throws Exception {
        Map.Entry<String, String> record;
        try {
            record = recordEvaluator.create(fields);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw e;
        }
        logger.debug("Record: {}", record);
        if(record != null) {
            try {
                sender.send(record);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw e;
            }
        }
    }
}