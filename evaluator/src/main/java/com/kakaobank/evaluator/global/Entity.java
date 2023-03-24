package com.kakaobank.evaluator.global;

import java.sql.SQLException;
import java.util.List;

public interface Entity {
  void createTableIfNotExists() throws SQLException;
  List<Entity> read(String... args) throws SQLException;
}
