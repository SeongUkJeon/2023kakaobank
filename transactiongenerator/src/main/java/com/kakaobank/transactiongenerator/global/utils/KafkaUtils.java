package com.kakaobank.transactiongenerator.global.utils;

import com.kakaobank.transactiongenerator.global.WithLogger;

import java.util.Properties;

public class KafkaUtils implements WithLogger {
    private KafkaUtils() {
        throw new IllegalStateException("Utility class");
    }

    // Kafka Util
    public static final String JOIN_GENERATOR = "com.kakaobank.transactiongenerator.generator.JoinGenerator";
    public static final String ACCOUNT_GENERATOR = "com.kakaobank.transactiongenerator.generator.AccountGenerator";
    public static final String DEPOSIT_GENERATOR = "com.kakaobank.transactiongenerator.generator.DepositGenerator";
    public static final String TRANSFER_GENERATOR = "com.kakaobank.transactiongenerator.generator.TransferGenerator";
    public static final String WITHDRAW_GENERATOR = "com.kakaobank.transactiongenerator.generator.WithdrawGenerator";
    public static final String CLUSTER = "localhost:9092";

    public static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", CLUSTER);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }
}