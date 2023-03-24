package com.kakaobank.evaluator.global.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.evaluator.global.Message;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleAMessage implements Message {
    @JsonProperty("rule") private static final String RULE = DetectionType.RuleA.name();
    @JsonProperty("customerNumber") private String customerNumber;
    @JsonProperty("customerName") private String customerName;
    @JsonProperty("customerBirth") private String customerBirth;
    @JsonProperty("accountNumber") private String accountNumber;
    @JsonProperty("accountOpenTime") private String accountOpenTime;
    @JsonProperty("totalDepositAmount") private String totalDepositAmount;
    @JsonProperty("totalWithdrawAmount") private String totalWithdrawAmount;

    public RuleAMessage(String customerNumber, String customerName, String customerBirth, String accountNumber, String accountOpenTime, String totalDepositAmount, String totalWithdrawAmount) {
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.customerBirth = customerBirth;
        this.accountNumber = accountNumber;
        this.accountOpenTime = accountOpenTime;
        this.totalDepositAmount = totalDepositAmount;
        this.totalWithdrawAmount = totalWithdrawAmount;
    }

    public String getRULE() {
        return RULE;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountOpenTime() {
        return accountOpenTime;
    }

    public String getCustomerBirth() {
        return customerBirth;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getTotalDepositAmount() {
        return totalDepositAmount;
    }

    public String getTotalWithdrawAmount() {
        return totalWithdrawAmount;
    }
}
