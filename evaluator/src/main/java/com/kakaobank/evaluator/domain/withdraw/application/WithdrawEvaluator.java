package com.kakaobank.evaluator.domain.withdraw.application;

import com.kakaobank.evaluator.domain.withdraw.domain.WithdrawAgg;
import com.kakaobank.evaluator.global.AbstractRecordEvaluator;
import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public class WithdrawEvaluator extends AbstractRecordEvaluator {
    private final Connection connection;
    public WithdrawEvaluator(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected FraudDetection saveFields(Map<String, String> fields) throws SQLException, ParseException {
        WithdrawAgg agg = new WithdrawAgg(connection, fields.get("amount"), fields.get("transactionTime"), fields.get("accountNumber"), fields.get("customerNumber"));

        return agg.detect();
    }
}