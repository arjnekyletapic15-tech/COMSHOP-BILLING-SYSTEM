package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public UserDao() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            defaultAdmin(conn);
        } catch (SQLException e) {
            System.err.println("Error seeding default admin: " + e.getMessage());
        }
    }

    public void defaultAdmin(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        String insertsql = "INSERT INTO users (username, password, roles, status) VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertsql);
                System.out.println("Default admin created successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/creating default admin: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password, String roles, String status) {
        String sql = "INSERT INTO users (username, password, roles, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, roles.toUpperCase());
            pstmt.setString(4, status.toUpperCase());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error occurred while registering user: " + e.getMessage());
        }
        return false;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if (!"APPROVED".equalsIgnoreCase(status)) {
                        return null; // PENDING or REJECTED accounts cannot log in
                    }
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            status
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error occurred while logging in: " + e.getMessage());
        }
        return null;
    }

    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toUpperCase());
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user status: " + e.getMessage());
        }
        return false;
    }

    public boolean approveUser(int adminId, int userId) {
        boolean success = updateUserStatus(userId, "APPROVED");
        if (success) {
            activityLogDao.logAction(adminId, "USER_APPROVED",
                    "Admin [" + adminId + "] approved account for User ID " + userId);
        }
        return success;
    }

    public boolean updateUserRoleAndStatus(int adminId, int userId, String role, String status) {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.toUpperCase());
            pstmt.setString(2, status.toUpperCase());
            pstmt.setInt(3, userId);

            if (pstmt.executeUpdate() > 0) {
                activityLogDao.logAction(adminId, "USER_UPDATED",
                        "Admin [" + adminId + "] changed role of User " + userId + " to "
                                + role.toUpperCase() + " (status: " + status.toUpperCase() + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user role/status: " + e.getMessage());
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    public List<User> getPendingUsers() {
        List<User> pendingUsers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pendingUsers.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending users: " + e.getMessage());
        }
        return pendingUsers;
    }
}