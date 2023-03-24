package com.kakaobank.evaluator.global.utils;

import com.kakaobank.evaluator.global.WithLogger;

import java.util.Properties;

public class KafkaUtils implements WithLogger {
    private KafkaUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ACCOUNT_EVALUATOR = "com.kakaobank.evaluator.domain.account.application.AccountEvaluator";
    public static final String DEPOSIT_EVALUATOR = "com.kakaobank.evaluator.domain.deposit.application.DepositEvaluator";
    public static final String JOIN_EVALUATOR = "com.kakaobank.evaluator.domain.join.application.JoinEvaluator";
    public static final String TRANSFER_EVALUATOR = "com.kakaobank.evaluator.domain.transfer.application.TransferEvaluator";
    public static final String WITHDRAW_EVALUATOR = "com.kakaobank.evaluator.domain.withdraw.application.WithdrawEvaluator";
    public static final int NUMBER_OF_CONSUMER = 5;
    private static final String CLUSTER = "localhost:9092";
    private static final String GROUP_NAME = "test";
    private static final String SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String DESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";

    public static Properties getConsumerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", CLUSTER);
        props.put("group.id", GROUP_NAME);
        props.put("enable.auto.commit", "false");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", DESERIALIZER);
        props.put("value.deserializer", DESERIALIZER);
        props.put("partition.assignment.strategy.config", "org.apache.kafka.clients.consumer.RoundRobinAssignor");
        props.put("max.poll.interval.ms.config", "1000000");
        props.put("max.poll.records.config", "10");

        return props;
    }

    public static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", CLUSTER);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", SERIALIZER);
        props.put("value.serializer", SERIALIZER);

        return props;
    }
}