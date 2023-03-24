package com.kakaobank.evaluator.global.utils;

import com.kakaobank.evaluator.global.WithLogger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils implements WithLogger {
    private DatabaseUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static String appHome;
    static {
        try {
            appHome = System.getProperty("app.home") == null ? new File(".").getCanonicalPath() : System.getProperty("app.home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String DATABASE_PATH = "jdbc:h2:file:" + appHome + "/data/h2_file;DB_CLOSE_ON_EXIT=false";
    private static final String DRIVER_NAME = "org.h2.Driver";

    private static Connection connection;
    static {
        try {
            Class.forName(DRIVER_NAME);
            connection = DriverManager.getConnection(DATABASE_PATH, "", "");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}