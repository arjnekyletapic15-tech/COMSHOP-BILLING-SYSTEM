package com.scc.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "roles TEXT NOT NULL, "
                + "status TEXT NOT NULL"
                + ");";

        String createActivityLogsTableSql = "CREATE TABLE IF NOT EXISTS activity_logs ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "action_type TEXT NOT NULL, "
                + "description TEXT NOT NULL, "
                + "timestamp TEXT NOT NULL DEFAULT (datetime('now','localtime')), "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";

        String createRentalsTableSql = "CREATE TABLE IF NOT EXISTS rentals ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "staff_id INTEGER NOT NULL, "
                + "customer_name TEXT NOT NULL, "
                + "pc_station TEXT NOT NULL, "
                + "minutes INTEGER NOT NULL, "
                + "total_amount REAL NOT NULL, "
                + "status TEXT NOT NULL, "
                + "created_at TEXT NOT NULL DEFAULT (datetime('now','localtime')), "
                + "FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createUsersTableSql);
            repairActivityLogsIfOutdated(stmt);
            stmt.execute(createActivityLogsTableSql);
            stmt.execute(createRentalsTableSql);
            System.out.println("[DatabaseInitializer] -> Database and tables initialized successfully!");
            System.out.println("[DatabaseInitializer] -> Using database file: "
                    + new java.io.File("comshoprecords.db").getAbsolutePath());

        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] -> Error initializing database: " + e.getMessage());
        }
    }


    private static void repairActivityLogsIfOutdated(Statement stmt) throws SQLException {
        List<String> existing = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(activity_logs)")) {
            while (rs.next()) {
                existing.add(rs.getString("name").toLowerCase());
            }
        }
        if (existing.isEmpty()) {
            return; // table does not exist yet
        }
        List<String> expected = List.of("id", "user_id", "action_type", "description", "timestamp");
        if (!existing.containsAll(expected)) {
            System.out.println("[DatabaseInitializer] -> Outdated activity_logs table found; rebuilding it.");
            stmt.execute("DROP TABLE activity_logs");
        }
    }
}