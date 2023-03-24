package com.kakaobank.transactiongenerator.generator;

import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.message.JoinRequest;

public class JoinGenerator extends AbstractRecordGenerator {
    @Override
    protected Sample kafkaMessageGen(Object... args) {
        logger.trace("Run kafkaMessageGen()");

        return new JoinRequest( (String) args[0], (String) args[1], (String) args[2], (String) args[3]);
    }
}