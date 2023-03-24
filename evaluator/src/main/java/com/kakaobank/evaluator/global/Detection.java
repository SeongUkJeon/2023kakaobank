package com.kakaobank.evaluator.global;

import com.kakaobank.evaluator.global.core.FraudDetection;

import java.sql.SQLException;
import java.text.ParseException;

public interface Detection {
    FraudDetection detect(String customerNumber) throws ParseException, SQLException;
}
