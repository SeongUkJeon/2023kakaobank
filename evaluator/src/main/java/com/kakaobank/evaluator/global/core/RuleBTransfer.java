package com.kakaobank.evaluator.global.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleBTransfer {
    @JsonProperty("remitAccountNumber") private String remitAccountNumber;
    @JsonProperty("receivingAccountNumber") private String receivingAccountNumber;
    @JsonProperty("transferAmount") private String transferAmount;
    @JsonProperty("transferTransactionTime") private String transferTransactionTime;

    public RuleBTransfer(String remitAccountNumber, String receivingAccountNumber, String transferAmount, String transferTransactionTime) {
        this.remitAccountNumber = remitAccountNumber;
        this.receivingAccountNumber = receivingAccountNumber;
        this.transferAmount = transferAmount;
        this.transferTransactionTime = transferTransactionTime;
    }

    public String getReceivingAccountNumber() {
        return receivingAccountNumber;
    }

    public String getRemitAccountNumber() {
        return remitAccountNumber;
    }

    public String getTransferAmount() {
        return transferAmount;
    }

    public String getTransferTransactionTime() {
        return transferTransactionTime;
    }
}
