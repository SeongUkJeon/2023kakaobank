package com.kakaobank.evaluator.global.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaobank.evaluator.global.LogType;
import com.kakaobank.evaluator.global.utils.DatabaseUtils;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.utils.KafkaUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.sql.Connection;
import java.time.Duration;
import java.util.*;

public class KafkaReceiver<K, V> implements Runnable, WithLogger {
    private Consumer<K, V> consumer = null;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final List<String> TOPIC_NAME = Collections.singletonList("fds.transactions");
    private final String threadName;

    public KafkaReceiver(long number) {
        logger.trace("Create KafkaReceiver");

        this.threadName = "consumer-thread-" + number;
        Properties props = KafkaUtils.getConsumerProperties();
        try {
            consumer = new KafkaConsumer<>(props);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        logger.info("[{}]Create a simulation. Start time: {}", threadName, new Date());

        consumer.subscribe(TOPIC_NAME);
        try {
            while (true) {
                ConsumerRecords<K, V> records = consumer.poll(Duration.ofSeconds(1));
                KafkaProfiler profiler = new KafkaProfiler();
                Connection connection = DatabaseUtils.getConnection();
                for (ConsumerRecord<K, V> record : records) {
                    try {
                        Map<String, String> fields = mapper.readValue((String) record.value(), Map.class);
                        logger.info("Receiving fields: {}", fields);
                        String type = fields.get("type");
                        if (type.equals(LogType.ACCOUNT.name())) {
                            profiler.setup(KafkaUtils.ACCOUNT_EVALUATOR, connection);
                        }
                        if (type.equals(LogType.DEPOSIT.name())) {
                            profiler.setup(KafkaUtils.DEPOSIT_EVALUATOR, connection);
                        }
                        if (type.equals(LogType.JOIN.name())) {
                            profiler.setup(KafkaUtils.JOIN_EVALUATOR, connection);
                        }
                        if (type.equals(LogType.TRANSFER.name())) {
                            profiler.setup(KafkaUtils.TRANSFER_EVALUATOR, connection);
                        }
                        if (type.equals(LogType.WITHDRAW.name())) {
                            profiler.setup(KafkaUtils.WITHDRAW_EVALUATOR, connection);
                        }
                        profiler.run(fields);
                        consumer.commitSync();
                    } catch(Exception e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                        consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
                    }
                }
            }
        } catch (WakeupException e) {
            logger.warn("{} trigger WakeupException.", threadName);
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}