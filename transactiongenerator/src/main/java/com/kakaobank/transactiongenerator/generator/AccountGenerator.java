package com.kakaobank.transactiongenerator.generator;

import com.kakaobank.transactiongenerator.message.AccountRequest;
import com.kakaobank.transactiongenerator.global.Sample;

public class AccountGenerator extends AbstractRecordGenerator {
    @Override
    protected Sample kafkaMessageGen(Object... args) {
        return new AccountRequest((String) args[0], (String) args[1], (String) args[2]);
    }
}