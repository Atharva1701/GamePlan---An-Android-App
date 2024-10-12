package com.example.gameplan;

public class Booking {
    private String bookingId;
    private String userId;

    private String courtId;
    private String date;
    private String timeSlot;
    private String sportName;

    public Booking() {
        // Default constructor required for calls to DataSnapshot.getValue(Booking.class)
    }

    public Booking(String sportName,String bookingId, String userId, String courtId, String date, String timeSlot) {
        this.sportName = sportName;
        this.bookingId = bookingId;
        this.userId = userId;
        this.courtId = courtId;
        this.date = date;
        this.timeSlot = timeSlot;
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getCourtId() {
        return courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
    public String getsportName() { return sportName;}

    public void setsportName(String sportName) {this.sportName = sportName;}
}
