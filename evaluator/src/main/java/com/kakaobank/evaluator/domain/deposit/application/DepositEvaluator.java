package com.kakaobank.evaluator.domain.deposit.application;

import com.kakaobank.evaluator.domain.deposit.domain.DepositAgg;
import com.kakaobank.evaluator.global.AbstractRecordEvaluator;
import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DepositEvaluator extends AbstractRecordEvaluator {
    private final Connection connection;
    public DepositEvaluator(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected FraudDetection saveFields(Map<String, String> fields) throws SQLException {
        new DepositAgg(connection, fields.get("amount"), fields.get("transactionTime"), fields.get("accountNumber"), fields.get("customerNumber"));
        return null;
    }
}