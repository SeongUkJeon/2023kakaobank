package com.kakaobank.transactiongenerator.global.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kakaobank.transactiongenerator.generator.RecordGenerator;
import com.kakaobank.transactiongenerator.global.Sender;
import com.kakaobank.transactiongenerator.global.WithLogger;

import java.util.Map;

public class KafkaSampler implements WithLogger {
    private static final Sender<Map.Entry<String, String>> sender = new KafkaSender<>();
    RecordGenerator<Map.Entry<String, String>> recordGenerator;

    public void setupTest(String generator) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        recordGenerator = (RecordGenerator<Map.Entry<String, String>>) Class.forName(generator).newInstance();
        logger.info("Sender: {}, Generator: {}", sender, recordGenerator);
    }

    public Boolean runTest(Object... args) throws Exception {
        Map.Entry<String, String> record;
        try {
            record = recordGenerator.create(args);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw e;
        }
        logger.debug("Record: {}", record);
        try {
            sender.send(record);
            return true;
        } catch(Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}