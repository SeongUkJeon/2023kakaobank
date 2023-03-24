package com.kakaobank.evaluator.global.kafka;

import com.kakaobank.evaluator.global.Sender;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.utils.KafkaUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Closeable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaSender<K, V> implements Sender<Map.Entry<K, V>>, Closeable, WithLogger {
    private Producer<K, V> producer = null;
    private static final String TOPIC_NAME = "fds.detections";

    public KafkaSender() {
        logger.trace("Run configure()");

        Properties props = KafkaUtils.getProducerProperties();
        try {
            producer = new KafkaProducer<>(props);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void send(Map.Entry<K, V> record) throws ExecutionException, InterruptedException {
        logger.debug("Record: {}", record);
        try {
            RecordMetadata metadata = producer.send(new ProducerRecord<>(TOPIC_NAME, record.getKey(), record.getValue())).get();
            logger.info("Message sent successfully. {} sent to {} Record metadata: {}", record.getValue(), TOPIC_NAME, metadata);
        } catch (ExecutionException | InterruptedException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void close() {
        if (this.producer != null) {
            producer.close();
        }
    }
}
