package com.app.triponeer;

import java.util.ArrayList;

public class Trip {
    String name;
    String Description;
    String sourceName;
    String destinationName;
    String sourceAddress;
    String destinationAddress;
    String status;
    String type;
    double distance;
    double sourceLat;
    double sourceLong;
    double destLat;
    double destLong;
    String date;
    String time;
    int day;
    int month;
    int year;
    int hour;
    int minute;
    String repeatPattern;
    ArrayList<String> repeatDays;
    ArrayList<String> notes;

    public Trip() {
        name = "";
        Description = "";
        sourceAddress = "";
        destinationAddress = "";
        sourceName = "";
        destinationName = "";
        status = "";
        sourceLat = 0;
        sourceLong = 0;
        destLat = 0;
        destLong = 0;
        type = "One Way";
        date = "";
        time = "";
        day = 0;
        month = 0;
        year = 0;
        hour = 0;
        minute = 0;
        repeatPattern = "";
        distance = 0;
        notes = new ArrayList<String>();
        repeatDays = new ArrayList<String>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setRepeatPattern(String repeatPattern) {
        this.repeatPattern = repeatPattern;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public void setRepeatDays(ArrayList<String> repeatDays) {
        this.repeatDays = repeatDays;
    }

    public void setDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void setSource(String sourceName, String sourceAddress, double sourceLat, double sourceLong) {
        this.sourceName = sourceName;
        this.sourceAddress = sourceAddress;
        this.sourceLat = sourceLat;
        this.sourceLong = sourceLong;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestination(String destinationName, String destinationAddress, double destLat, double destLong) {
        this.destinationName = destinationName;
        this.destinationAddress = destinationAddress;
        this.destLat = destLat;
        this.destLong = destLong;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return Description;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public double getSourceLat() {
        return sourceLat;
    }

    public double getSourceLong() {
        return sourceLong;
    }

    public double getDestLat() {
        return destLat;
    }

    public double getDestLong() {
        return destLong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getRepeatPattern() {
        return repeatPattern;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public ArrayList<String> getRepeatDays() {
        return repeatDays;
    }

}
