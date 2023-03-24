package com.kakaobank.evaluator.global.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.evaluator.global.Message;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleBMessage implements Message {
    @JsonProperty("rule") private static final String RULE = DetectionType.RuleB.name();
    @JsonProperty("customerNumber") private String customerNumber;
    @JsonProperty("customerName") private String customerName;
    @JsonProperty("customerBirth") private String customerBirth;
    @JsonProperty("transfers") private List<RuleBTransfer> transfers;

    public RuleBMessage(String customerNumber, String customerName, String customerBirth, List<RuleBTransfer> transfers) {
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.customerBirth = customerBirth;
        this.transfers = transfers;
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

    public List<RuleBTransfer> getTransfers() {
        return transfers;
    }

    public String getRULE() {
        return RULE;
    }
}