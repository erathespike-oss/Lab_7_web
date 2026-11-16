package kz.iitu.registration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kz.iitu.registration.model.EventInfo;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final List<EventInfo> events = new ArrayList<>(List.of(
            new EventInfo(1, "Web Design Workshop", "Room 101"),
            new EventInfo(2, "JavaScript Quiz", "Room 202"),
            new EventInfo(3, "IT Career Talk", "Conference Hall")
    ));
    private long nextId = 4;

    public String getWelcomeMessage() {
        return "Welcome to the Event Registration API";
    }

    public synchronized List<EventInfo> getEvents(String title) {
        if (title == null) {
            return new ArrayList<>(events);
        }
        List<EventInfo> matches = new ArrayList<>();
        String query = title.toLowerCase(Locale.ROOT);
        for (EventInfo event : events) {
            if (event.getTitle().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(event);
            }
        }
        return matches;
    }

    public synchronized EventInfo createEvent(String title, String location) {
        EventInfo event = new EventInfo(nextId++, title, location);
        events.add(event);
        return event;
    }

    public synchronized EventInfo updateEvent(long id, String title, String location) {
        for (int index = 0; index < events.size(); index++) {
            if (events.get(index).getId() == id) {
                EventInfo event = new EventInfo(id, title, location);
                events.set(index, event);
                return event;
            }
        }
        return null;
    }

    public synchronized boolean deleteEvent(long id) {
        for (int index = 0; index < events.size(); index++) {
            if (events.get(index).getId() == id) {
                events.remove(index);
                return true;
            }
        }
        return false;
    }

    public synchronized EventInfo getEventById(long id) {
        for (EventInfo event : events) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }
}
