package com.kakaobank.evaluator.global;

import java.util.concurrent.ExecutionException;

public interface Sender<Record> {
    void send(Record record) throws ExecutionException, InterruptedException;
}