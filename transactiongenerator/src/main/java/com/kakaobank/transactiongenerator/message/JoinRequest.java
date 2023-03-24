package com.kakaobank.transactiongenerator.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaobank.transactiongenerator.global.LogType;
import com.kakaobank.transactiongenerator.global.Sample;
import com.kakaobank.transactiongenerator.global.WithLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinRequest implements Sample, WithLogger {
    @JsonProperty("type") private static final String TYPE = LogType.JOIN.name();
    @JsonProperty("time") private String time;
    @JsonProperty("customerNumber") private String customerNumber;
    @JsonProperty("customerName") private String customerName;
    @JsonProperty("customerBirth" ) private String customerBirth;

    public JoinRequest(String time, String customerNumber, String customerName, String customerBirth) {
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
}