package com.kakaobank.evaluator.domain.transfer.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.evaluator.global.Entity;
import com.kakaobank.evaluator.global.LogType;
import com.kakaobank.evaluator.global.Message;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.core.FraudDetection;
import com.kakaobank.evaluator.global.core.RuleBDetection;
import com.kakaobank.evaluator.global.repository.JdbcTemplate;
import com.kakaobank.evaluator.global.core.RuleADetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferAgg implements Entity, WithLogger {
    static final String TABLE_NAME = "TRANSFER_LOG";
    static final String SQL_CREATE_TRANSFER = "CREATE TABLE " + TABLE_NAME + " ( `ID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `AMOUNT` VARCHAR(15), `TRANSACTION_TIME` VARCHAR(13), `CUSTOMER_NUMBER` VARCHAR(50), `REMIT_ACCOUNT_NUMBER` VARCHAR(14), `RECEIVING_BANK` VARCHAR(15), `RECEIVING_ACCOUNT_NUMBER` VARCHAR(14), `RECEIVING_ACCOUNT_HOLDER` VARCHAR(50))";
    static final String SQL_INSERT_TRANSFER = "INSERT INTO " + TABLE_NAME + " ( `AMOUNT`, `TRANSACTION_TIME`, `CUSTOMER_NUMBER`, `REMIT_ACCOUNT_NUMBER`, `RECEIVING_BANK`, `RECEIVING_ACCOUNT_NUMBER`, `RECEIVING_ACCOUNT_HOLDER` ) VALUES (?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_SELECT_TRANSFER = "SELECT `ID`, `AMOUNT`, `TRANSACTION_TIME`, `CUSTOMER_NUMBER`, `REMIT_ACCOUNT_NUMBER`, `RECEIVING_BANK`, `RECEIVING_ACCOUNT_NUMBER`, `RECEIVING_ACCOUNT_HOLDER` FROM " + TABLE_NAME + " WHERE `CUSTOMER_NUMBER` = ? AND `REMIT_ACCOUNT_NUMBER` = ? AND `TRANSACTION_TIME` BETWEEN ? AND ?";

    @JsonProperty("type") private static final String TYPE = LogType.TRANSFER.name();
    @JsonProperty("amount") private String amount;
    @JsonProperty("transactionTime") private String transactionTime;
    @JsonProperty("customerNumber") private String customerNumber;
    @JsonProperty("remitAccountNumber") private String remitAccountNumber;
    @JsonProperty("receivingBank") private String receivingBank;
    @JsonProperty("receivingAccountNumber") private String receivingAccountNumber;
    @JsonProperty("receivingAccountHolder") private String receivingAccountHolder;

    private JdbcTemplate template;

    public TransferAgg(Connection connection) {
        this.template = new JdbcTemplate(connection);
    }

    public TransferAgg(Connection connection, String amount, String transactionTime, String customerNumber, String remitAccountNumber, String receivingBank, String receivingAccountNumber, String receivingAccountHolder) throws SQLException {
        this.template = new JdbcTemplate(connection);
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.customerNumber = customerNumber;
        this.remitAccountNumber = remitAccountNumber;
        this.receivingBank = receivingBank;
        this.receivingAccountNumber = receivingAccountNumber;
        this.receivingAccountHolder = receivingAccountHolder;

        createTableIfNotExists();
        template.prepare(SQL_INSERT_TRANSFER)
            .args(amount, transactionTime, customerNumber, remitAccountNumber, receivingBank, receivingAccountNumber, receivingAccountHolder)
            .update();
    }

    private TransferAgg(String amount, String transactionTime, String customerNumber, String remitAccountNumber, String receivingBank, String receivingAccountNumber, String receivingAccountHolder) {
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.customerNumber = customerNumber;
        this.remitAccountNumber = remitAccountNumber;
        this.receivingBank = receivingBank;
        this.receivingAccountNumber = receivingAccountNumber;
        this.receivingAccountHolder = receivingAccountHolder;
    }

    public String getType() {
        return TYPE;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getRemitAccountNumber() {
        return remitAccountNumber;
    }

    public String getReceivingBank() {
        return receivingBank;
    }

    public String getReceivingAccountNumber() {
        return receivingAccountNumber;
    }

    public String getReceivingAccountHolder() {
        return receivingAccountHolder;
    }

    @Override
    public void createTableIfNotExists() throws SQLException {
        logger.trace("Run createTableIfNotExists()");
        template.createTableIfNotExists(TABLE_NAME, SQL_CREATE_TRANSFER);
    }

    @Override
    public List<Entity> read(String... args) throws SQLException {
        Iterable<List<Object>> iterable = template.prepare(SQL_SELECT_TRANSFER)
            .args(args)
            .resultTypes(Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class)
            .query();

        return StreamSupport.stream(iterable.spliterator(), false)
            .map(element -> new TransferAgg((String) element.get(1), (String) element.get(2), (String) element.get(3), (String) element.get(4), (String) element.get(5), (String) element.get(6), (String) element.get(7)))
            .collect(Collectors.toList());
    }

    public FraudDetection detect() throws SQLException, ParseException {
        long start = System.currentTimeMillis();
        RuleADetection ruleA = RuleADetection.getInstance();
        FraudDetection ruleAResult = ruleA.detect(customerNumber);
        RuleBDetection ruleB = RuleBDetection.getInstance();
        FraudDetection ruleBResult = ruleB.detect(customerNumber);
        long end = System.currentTimeMillis();
        logger.info("Time spent detecting: {}", end - start);

        if(ruleAResult != null && ruleBResult != null) {
            List<Message> results = ruleBResult.getResults();
            results.addAll(ruleAResult.getResults());
            return new FraudDetection(results);
        } else if(ruleAResult != null) {
            return ruleAResult;
        } else {
            return ruleBResult;
        }
    }
}