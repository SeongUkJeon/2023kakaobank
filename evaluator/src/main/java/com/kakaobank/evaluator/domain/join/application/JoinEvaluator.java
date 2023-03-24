package com.kakaobank.evaluator.domain.join.application;

import com.kakaobank.evaluator.domain.join.domain.JoinAgg;
import com.kakaobank.evaluator.global.AbstractRecordEvaluator;
import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public class JoinEvaluator extends AbstractRecordEvaluator {
    private final Connection connection;
    public JoinEvaluator(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected FraudDetection saveFields(Map<String, String> fields) throws SQLException, ParseException {
        new JoinAgg(connection, fields.get("time"), fields.get("customerNumber"), fields.get("customerName"), fields.get("customerBirth"));
        return null;
    }
}