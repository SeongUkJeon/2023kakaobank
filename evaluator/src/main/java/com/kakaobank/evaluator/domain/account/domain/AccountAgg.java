package com.kakaobank.evaluator.domain.account.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.evaluator.global.Entity;
import com.kakaobank.evaluator.global.LogType;
import com.kakaobank.evaluator.global.WithLogger;
import com.kakaobank.evaluator.global.repository.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountAgg implements Entity, WithLogger {
    static final String TABLE_NAME = "ACCOUNT_LOG";
    static final String SQL_CREATE_ACCOUNT = "CREATE TABLE " + TABLE_NAME + " ( `ID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `NUMBER` VARCHAR(14), `TRANSACTION_TIME` VARCHAR(13), `CUSTOMER_NUMBER` VARCHAR(50))";
    static final String SQL_INSERT_ACCOUNT = "INSERT INTO " + TABLE_NAME + " ( `NUMBER`, `TRANSACTION_TIME`, `CUSTOMER_NUMBER` ) VALUES (?, ?, ?)";
    static final String SQL_SELECT_ACCOUNT = "SELECT `ID`, `NUMBER`, `TRANSACTION_TIME`, `CUSTOMER_NUMBER` FROM " + TABLE_NAME + " WHERE `CUSTOMER_NUMBER` = ?";

    @JsonProperty("type") private static final String TYPE = LogType.ACCOUNT.name();
    @JsonProperty("number") private String number;
    @JsonProperty("transactionTime") private String transactionTime;
    @JsonProperty("customerNumber") private String customerNumber;

    private JdbcTemplate template;

    public AccountAgg(Connection connection) {
        this.template = new JdbcTemplate(connection);
    }

    public AccountAgg(Connection connection, String number, String transactionTime, String customerNumber) throws SQLException {
        this.template = new JdbcTemplate(connection);
        this.number = number;
        this.transactionTime = transactionTime;
        this.customerNumber = customerNumber;

        createTableIfNotExists();
        template.prepare(SQL_INSERT_ACCOUNT)
            .args(number, transactionTime, customerNumber)
            .update();
    }

    private AccountAgg(String number, String transactionTime, String customerNumber) {
        this.number = number;
        this.transactionTime = transactionTime;
        this.customerNumber = customerNumber;
    }

    public String getType() {
        return TYPE;
    }

    public String getNumber() {
        return number;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    @Override
    public void createTableIfNotExists() throws SQLException {
        logger.trace("Run createTableIfNotExists()");
        template.createTableIfNotExists(TABLE_NAME, SQL_CREATE_ACCOUNT);
    }

    @Override
    public List<Entity> read(String... args) throws SQLException {
        Iterable<List<Object>> iterable = template.prepare(SQL_SELECT_ACCOUNT)
            .args(args)
            .resultTypes(Integer.class, String.class, String.class, String.class)
            .query();

        return StreamSupport.stream(iterable.spliterator(), false)
            .map(element -> new AccountAgg((String) element.get(1), (String) element.get(2), (String) element.get(3)))
            .collect(Collectors.toList());
    }
}