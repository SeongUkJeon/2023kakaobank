package com.kakaobank.evaluator.global.repository;

import com.kakaobank.evaluator.global.WithLogger;

import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.function.IntFunction;

public class JdbcTemplate implements WithLogger {
  private final Connection connection;

  public JdbcTemplate(Connection connection) {
    this.connection = connection;
  }

  public void createTableIfNotExists(String table, String sql) throws SQLException {
    logger.debug("Table: {}, SQL: {}", table, sql);
    ResultSet rs = connection.getMetaData().getTables(null, null, table, new String[] { "TABLE" });

    if (!rs.next()) {
      PreparedStatement ps = connection.prepareStatement(sql);
      ps.execute();
      logger.info("{} table created", table);
    } else {
      logger.info("{} table already exists", table);
    }
  }

  public PS prepare(String sql) {
    return new PS(connection, sql);
  }

  public static class PS {
    Connection connection;
    String sql;

    public PS(Connection connection, String sql) {
      this.connection = connection;
      this.sql = sql;
    }

    List<?> args = null;
    List<Class<?>> resultTypes = null;

    public PS args(Object... args) {
      this.args = Arrays.asList(args);
      return this;
    }

    public PS resultTypes(Class<?>... resultTypes) {
      this.resultTypes = Arrays.asList(resultTypes);
      return this;
    }

    public Iterable<List<Object>> query() throws SQLException {
      PreparedStatement ps = connection.prepareStatement(sql);
      configureArguments(ps);

      List<List<Object>> result = new ArrayList<>();
      ResultSet rs = ps.executeQuery();
      Map<Class<?>, IntFunction<?>> mapper = new HashMap<>();
      mapper.put(String.class, x -> {
        try {
          return rs.getString(x);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return x;
      });
      mapper.put(Integer.class, x -> {
        try {
          return rs.getInt(x);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return x;
      });
      mapper.put(Date.class, x -> {
        try {
          return rs.getDate(x);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return x;
      });
      mapper.put(Timestamp.class, x -> {
        try {
          return rs.getTimestamp(x);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return x;
      });

      ResultSetMetaData meta = rs.getMetaData();
      while (rs.next()) {
        List<Object> functionResults = new ArrayList<>();
        for (int i = 0; i < meta.getColumnCount(); i++) {
          IntFunction<?> extractor = mapper.get(resultTypes.get(i)) != null ? mapper.get(resultTypes.get(i)) : x -> {
            try {
              return rs.getInt(x);
            } catch (SQLException e) {
              e.printStackTrace();
            }
            return x;
          };
          functionResults.add(extractor.apply(i + 1));
        }
        result.add(functionResults);
      }
      logger.info("Query result: {}", result);

      return result;
    }

    public int update() throws SQLException {
      PreparedStatement ps = connection.prepareStatement(sql);
      configureArguments(ps);
      int result = ps.executeUpdate();
      logger.info("{} row(s) updated", result);

      return result;
    }

    private void configureArguments(PreparedStatement ps) throws SQLException {
      logger.debug("Arguments: {}", args);

      for(int i = 0; i < args.size(); i++) {
        Object value = args.get(i);
        if(value instanceof String) ps.setString(i + 1, (String) value);
        else if(value instanceof Integer) ps.setInt(i + 1, (int) value);
        else if(value instanceof Date) ps.setDate(i + 1, new java.sql.Date((((Date) value).getTime())));
        else throw new IllegalArgumentException("Unknown type: " + value.getClass().getName());
      }
    }
  }
}