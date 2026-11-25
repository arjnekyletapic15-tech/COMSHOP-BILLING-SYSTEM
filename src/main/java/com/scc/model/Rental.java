package com.scc.model;

public record Rental(
        int id,
        int staffId,
        String customerName,
        String pcStation,
        int minutes,
        double totalAmount,
        String status,
        String createdAt
) {
}