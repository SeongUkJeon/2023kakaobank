package com.kakaobank.evaluator.domain.transfer.application;

import com.kakaobank.evaluator.domain.transfer.domain.TransferAgg;
import com.kakaobank.evaluator.global.AbstractRecordEvaluator;
import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

public class TransferEvaluator extends AbstractRecordEvaluator {
    private final Connection connection;
    public TransferEvaluator(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected FraudDetection saveFields(Map<String, String> fields) throws SQLException, ParseException {
        TransferAgg agg = new TransferAgg(
                connection, fields.get("amount"), fields.get("transactionTime"),
                fields.get("customerNumber"), fields.get("remitAccountNumber"),
                fields.get("receivingBank"), fields.get("receivingAccountNumber"),
                fields.get("receivingAccountHolder"));

        return agg.detect();
    }
}