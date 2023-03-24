package com.kakaobank.transactiongenerator.generator;

import com.kakaobank.transactiongenerator.message.DepositRequest;
import com.kakaobank.transactiongenerator.global.Sample;

public class DepositGenerator extends AbstractRecordGenerator {
    @Override
    protected Sample kafkaMessageGen(Object... args) {
        return new DepositRequest((String) args[0], (String) args[1], (String) args[2], (String) args[3]);
    }
}