package com.example.gameplan;

public class Event {
    private String eventId;
    private String title;
    private String description;
    private String time;
    private String date;
    private String location;
    private String imageUrl;

    public Event() {
        // Default constructor required for Firebase
    }

    public Event(String eventId, String title, String description, String time, String date, String location, String imageUrl) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.time = time;
        this.date = date;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

