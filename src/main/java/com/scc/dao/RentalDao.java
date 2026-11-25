package com.scc.dao;

import com.scc.model.Rental;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RentalDao {

    public static final double RATE_PER_HOUR = 40.0;

    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public int processRental(int staffId, String customerName, String pcStation, int minutes) {
        String sql = "INSERT INTO rentals (staff_id, customer_name, pc_station, minutes, total_amount, status) "
                + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        double total = RATE_PER_HOUR * minutes / 60.0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, staffId);
            pstmt.setString(2, customerName);
            pstmt.setString(3, pcStation);
            pstmt.setInt(4, minutes);
            pstmt.setDouble(5, total);
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int rentalId = keys.getInt(1);
                    activityLogDao.logAction(staffId, "RENTAL_PROCESSED",
                            String.format("Processed rental #%d for %s - %s, %d min - Total: P%.2f",
                                    rentalId, customerName, pcStation, minutes, total));
                    return rentalId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error processing rental: " + e.getMessage());
        }
        return -1;
    }

    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, staff_id, customer_name, pc_station, minutes, total_amount, status, created_at "
                + "FROM rentals ORDER BY created_at DESC, id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(new Rental(
                        rs.getInt("id"),
                        rs.getInt("staff_id"),
                        rs.getString("customer_name"),
                        rs.getString("pc_station"),
                        rs.getInt("minutes"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rentals: " + e.getMessage());
        }
        return rentals;
    }

    public boolean updateRentalStatus(int staffId, int rentalId, String newStatus) {
        String selectSql = "SELECT status FROM rentals WHERE id = ?";
        String updateSql = "UPDATE rentals SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            String oldStatus;
            try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
                sel.setInt(1, rentalId);
                try (ResultSet rs = sel.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    oldStatus = rs.getString("status");
                }
            }

            try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                upd.setString(1, newStatus.toUpperCase());
                upd.setInt(2, rentalId);
                if (upd.executeUpdate() > 0) {
                    activityLogDao.logAction(staffId, "RENTAL_STATUS_UPDATED",
                            "Updated Rental #" + rentalId + " status from " + oldStatus
                                    + " to " + newStatus.toUpperCase());
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating rental status: " + e.getMessage());
        }
        return false;
    }
}