package com.kakaobank.evaluator.domain.withdraw.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.evaluator.global.Entity;
import com.kakaobank.evaluator.global.LogType;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.core.FraudDetection;
import com.kakaobank.evaluator.global.repository.JdbcTemplate;
import com.kakaobank.evaluator.global.core.RuleADetection;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawAgg implements Entity, WithLogger {
    static final String TABLE_NAME = "WITHDRAW_LOG";
    static final String SQL_CREATE_WITHDRAW = "CREATE TABLE " + TABLE_NAME + " ( `ID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `AMOUNT` VARCHAR(15), `TRANSACTION_TIME` VARCHAR(13), `ACCOUNT_NUMBER` VARCHAR(14), `CUSTOMER_NUMBER` VARCHAR(50))";
    static final String SQL_INSERT_WITHDRAW = "INSERT INTO " + TABLE_NAME + " ( `AMOUNT`, `TRANSACTION_TIME`, `ACCOUNT_NUMBER`, `CUSTOMER_NUMBER` ) VALUES (?, ?, ?, ?)";
    static final String SQL_SELECT_WITHDRAW = "SELECT `ID`, `AMOUNT`, `TRANSACTION_TIME`, `ACCOUNT_NUMBER`, `CUSTOMER_NUMBER` FROM " + TABLE_NAME + " WHERE `CUSTOMER_NUMBER` = ? AND `ACCOUNT_NUMBER` = ? AND `TRANSACTION_TIME` BETWEEN ? AND ?";

    @JsonProperty("type") private static final String TYPE = LogType.WITHDRAW.name();
    @JsonProperty("amount") private String amount;
    @JsonProperty("transactionTime") private String transactionTime;
    @JsonProperty("accountNumber") private String accountNumber;
    @JsonProperty("customerNumber") private String customerNumber;

    private JdbcTemplate template;

    public WithdrawAgg(Connection connection) {
        this.template = new JdbcTemplate(connection);
    }

    public WithdrawAgg(Connection connection, String amount, String transactionTime, String accountNumber, String customerNumber) throws SQLException {
        this.template = new JdbcTemplate(connection);
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;

        createTableIfNotExists();
        template.prepare(SQL_INSERT_WITHDRAW)
            .args(amount, transactionTime, accountNumber, customerNumber)
            .update();
    }

    private WithdrawAgg(String amount, String transactionTime, String accountNumber, String customerNumber) {
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    @Override
    public void createTableIfNotExists() throws SQLException {
        logger.trace("Run createTableIfNotExists()");
        template.createTableIfNotExists(TABLE_NAME, SQL_CREATE_WITHDRAW);
    }

    @Override
    public List<Entity> read(String... args) throws SQLException {
        Iterable<List<Object>> iterable = template.prepare(SQL_SELECT_WITHDRAW)
            .args(args)
            .resultTypes(Integer.class, String.class, String.class, String.class, String.class)
            .query();

        return StreamSupport.stream(iterable.spliterator(), false)
            .map(element -> new WithdrawAgg((String) element.get(1), (String) element.get(2), (String) element.get(3), (String) element.get(4)))
            .collect(Collectors.toList());
    }

    public FraudDetection detect() throws SQLException, ParseException {
        long start = System.currentTimeMillis();
        RuleADetection ruleA = RuleADetection.getInstance();
        FraudDetection result = ruleA.detect(customerNumber);
        long end = System.currentTimeMillis();
        logger.info("Time spent detecting: {}", end - start);

        return result;
    }
}