package com.example.apexwh.objects;

public class HistoryRecord {

    public String date, type, userId, data;

    public HistoryRecord(String date, String type, String userId, String data) {
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.data = data;
    }
}
