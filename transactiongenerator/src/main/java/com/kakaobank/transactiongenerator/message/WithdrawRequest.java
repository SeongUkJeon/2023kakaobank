package com.kakaobank.transactiongenerator.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.transactiongenerator.global.LogType;
import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.global.WithLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawRequest implements Sample, WithLogger {
    @JsonProperty("type") private static final String TYPE = LogType.WITHDRAW.name();
    @JsonProperty("amount") private String amount;
    @JsonProperty("transactionTime") private String transactionTime;
    @JsonProperty("accountNumber") private String accountNumber;
    @JsonProperty("customerNumber") private String customerNumber;

    public WithdrawRequest(String amount, String transactionTime, String accountNumber, String customerNumber) {
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
}