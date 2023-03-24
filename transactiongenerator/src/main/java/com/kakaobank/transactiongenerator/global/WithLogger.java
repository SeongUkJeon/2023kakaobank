package com.kakaobank.transactiongenerator.global;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface WithLogger {
  @JsonIgnore
  Logger logger = LoggerFactory.getLogger(WithLogger.class);
}