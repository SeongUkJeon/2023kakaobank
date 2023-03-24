package com.kakaobank.transactiongenerator.generator;

import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.message.TransferRequest;

public class TransferGenerator extends AbstractRecordGenerator {
    @Override
    protected Sample kafkaMessageGen(Object... args) {
        return new TransferRequest((String) args[0], (String) args[1], (String) args[2], (String) args[3], (String) args[4], (String) args[5], (String) args[6]);
    }
}