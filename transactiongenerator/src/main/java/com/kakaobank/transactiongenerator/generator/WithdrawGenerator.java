package com.kakaobank.transactiongenerator.generator;

import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.message.WithdrawRequest;

public class WithdrawGenerator extends AbstractRecordGenerator {
    @Override
    protected Sample kafkaMessageGen(Object... args) {
        return new WithdrawRequest((String) args[0], (String) args[1], (String) args[2], (String) args[3]);
    }
}