package com.kakaobank.transactiongenerator.exec;

import com.kakaobank.transactiongenerator.global.Simulator;
import com.kakaobank.transactiongenerator.global.WithLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransactionGenerator implements WithLogger {
    public static void main(String[] args) {
        logger.info("Running the transaction generator.");
        int loopCnt = Integer.parseInt(args[0]);
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(loopCnt);

        for(int i = 0; i < loopCnt; i++){
            Simulator task = new Simulator(i);
            scheduledThreadPool.schedule(task,0, TimeUnit.SECONDS);
        }

        scheduledThreadPool.shutdown();
        logger.info("Shutting down the transaction generator.");
    }
}