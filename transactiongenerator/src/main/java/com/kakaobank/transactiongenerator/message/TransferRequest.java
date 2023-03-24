package com.kakaobank.transactiongenerator.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.transactiongenerator.global.LogType;
import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.global.WithLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferRequest implements Sample, WithLogger {
    @JsonProperty("type") private static final String TYPE = LogType.TRANSFER.name();
    @JsonProperty("amount") private String amount;
    @JsonProperty("transactionTime") private String transactionTime;
    @JsonProperty("customerNumber") private String customerNumber;
    @JsonProperty("remitAccountNumber") private String remitAccountNumber;
    @JsonProperty("receivingBank") private String receivingBank;
    @JsonProperty("receivingAccountNumber") private String receivingAccountNumber;
    @JsonProperty("receivingAccountHolder") private String receivingAccountHolder;

    public TransferRequest(String amount, String transactionTime, String customerNumber, String remitAccountNumber, String receivingBank, String receivingAccountNumber, String receivingAccountHolder) {
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
}