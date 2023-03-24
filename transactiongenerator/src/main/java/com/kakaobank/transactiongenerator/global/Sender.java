package com.kakaobank.transactiongenerator.global;

import java.util.concurrent.ExecutionException;

public interface Sender<Record> {
    void send(Record record) throws ExecutionException, InterruptedException;
}