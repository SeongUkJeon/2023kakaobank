package com.kakaobank.evaluator.domain.account.application;

import com.kakaobank.evaluator.domain.account.domain.AccountAgg;
import com.kakaobank.evaluator.global.AbstractRecordEvaluator;
import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class AccountEvaluator extends AbstractRecordEvaluator {
    private final Connection connection;
    public AccountEvaluator(Connection connection) {
      this.connection = connection;
    }

    @Override
    protected FraudDetection saveFields(Map<String, String> fields) throws SQLException {
        new AccountAgg(connection, fields.get("number"), fields.get("transactionTime"), fields.get("customerNumber"));
        return null;
    }
}