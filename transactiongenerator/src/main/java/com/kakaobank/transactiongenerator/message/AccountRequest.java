package com.kakaobank.transactiongenerator.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.transactiongenerator.global.LogType;
import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.global.WithLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountRequest implements Sample, WithLogger {
    @JsonProperty("type") private static final String TYPE = LogType.ACCOUNT.name();
    @JsonProperty("number") private String number;
    @JsonProperty("transactionTime") private String transactionTime;
    @JsonProperty("customerNumber") private String customerNumber;

    public AccountRequest(String number, String transactionTime, String customerNumber) {
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
}