package com.kakaobank.evaluator.domain.join.domain;

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
public class JoinAgg implements Entity, WithLogger {
    static final String TABLE_NAME = "JOIN_LOG";
    static final String SQL_CREATE_JOIN = "CREATE TABLE " + TABLE_NAME + " ( `TIME` VARCHAR(13), `CUSTOMER_NUMBER` VARCHAR(50) NOT NULL PRIMARY KEY, `CUSTOMER_NAME` VARCHAR(10), `CUSTOMER_BIRTH` VARCHAR(10))";
    static final String SQL_INSERT_JOIN = "INSERT INTO " + TABLE_NAME + " ( `TIME`, `CUSTOMER_NUMBER`, `CUSTOMER_NAME`, `CUSTOMER_BIRTH` ) VALUES (?, ?, ?, ?)";
    static final String SQL_SELECT_JOIN = "SELECT `TIME`, `CUSTOMER_NUMBER`, `CUSTOMER_NAME`, `CUSTOMER_BIRTH` FROM " + TABLE_NAME + " WHERE `CUSTOMER_NUMBER` = ?";

    @JsonProperty("type") private static final String TYPE = LogType.JOIN.name();
    @JsonProperty("time") private String time;
    @JsonProperty("customerNumber") private String customerNumber;
    @JsonProperty("customerName") private String customerName;
    @JsonProperty("customerBirth" ) private String customerBirth;

    private JdbcTemplate template;

    public JoinAgg(Connection connection) {
        this.template = new JdbcTemplate(connection);
    }

    public JoinAgg(Connection connection, String time, String customerNumber, String customerName, String customerBirth) throws SQLException {
        this.template = new JdbcTemplate(connection);
        this.time = time;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.customerBirth = customerBirth;

        createTableIfNotExists();
        template.prepare(SQL_INSERT_JOIN)
            .args(time, customerNumber, customerName, customerBirth)
            .update();
    }

    private JoinAgg(String time, String customerNumber, String customerName, String customerBirth) {
        this.time = time;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.customerBirth = customerBirth;
    }

    public String getType() {
        return TYPE;
    }

    public String getTime() {
        return time;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerBirth() {
        return customerBirth;
    }

    @Override
    public void createTableIfNotExists() throws SQLException {
        logger.trace("Run createTableIfNotExists()");
        template.createTableIfNotExists(TABLE_NAME, SQL_CREATE_JOIN);
    }

    @Override
    public List<Entity> read(String...  args) throws SQLException {
        Iterable<List<Object>> iterable = template.prepare(SQL_SELECT_JOIN)
            .args(args)
            .resultTypes(String.class, String.class, String.class, String.class)
            .query();

        return StreamSupport.stream(iterable.spliterator(), false)
            .map(element -> new JoinAgg((String) element.get(0), (String) element.get(1), (String) element.get(2), (String) element.get(3)))
            .collect(Collectors.toList());
    }
}