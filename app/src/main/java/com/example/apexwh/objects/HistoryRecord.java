package com.example.apexwh.objects;

public class HistoryRecord {

    public String date, type, userId, data;

    public int mode;

    public HistoryRecord(String date, String type, String userId, String data, int mode) {
        this.date = date;
        this.type = type;
        this.userId = userId;
        this.data = data;
        this.mode = mode;
    }

    public HistoryRecord(String date, String type, String userId, String data) {

        this(date, type, userId, data, 0);

    }
}
