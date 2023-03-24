package com.kakaobank.evaluator.global;

import java.util.concurrent.ExecutionException;

public interface Receiver<Record> {
    void receive(Record record) throws ExecutionException, InterruptedException;
}