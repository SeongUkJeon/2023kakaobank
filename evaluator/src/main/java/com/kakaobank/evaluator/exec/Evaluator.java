package com.kakaobank.evaluator.exec;

import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.kafka.KafkaReceiver;
import com.kakaobank.evaluator.global.utils.KafkaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Evaluator implements WithLogger {
    private static final List<KafkaReceiver<String, String>> workerThreads = new ArrayList<>();

    public static void main(String[] args) {
        logger.info("Running the evaluator.");
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < KafkaUtils.NUMBER_OF_CONSUMER; i++) {
            KafkaReceiver<String, String> receiver = new KafkaReceiver<>(i);
            workerThreads.add(receiver);
            executorService.execute(receiver);
        }
    }

    static class ShutdownThread extends Thread {
        @Override
        public void run() {
            workerThreads.forEach(KafkaReceiver::shutdown);
            logger.info("Exit the program.");
        }
    }
}