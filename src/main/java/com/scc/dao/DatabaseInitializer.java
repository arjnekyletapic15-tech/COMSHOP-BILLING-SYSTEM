package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "roles TEXT NOT NULL, "
                + "status TEXT NOT NULL"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createUsersTableSql);
            System.out.println("[DatabaseInitializer] -> Database and tables initialized successfully!");

        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] -> Error initializing database: " + e.getMessage());
        }
    }
}