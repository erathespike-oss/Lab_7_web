package kz.iitu.registration.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class EventInfo {
    private long id;
    private String title;
    private String location;

    @JsonCreator
    public EventInfo() {
    }

    public EventInfo(long id, String title, String location) {
        this.id = id;
        this.title = title;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
