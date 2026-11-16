package kz.iitu.registration.controller;

import java.util.List;
import java.util.Map;
import kz.iitu.registration.model.EventInfo;
import kz.iitu.registration.service.EventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final EventService eventService;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${app.course}")
    private String course;

    @Value("${app.semester}")
    private int semester;

    public RegistrationController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", eventService.getWelcomeMessage());
    }

    @GetMapping("/events")
    public List<EventInfo> events(@RequestParam(name = "title", required = false) String title) {
        return eventService.getEvents(title);
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody EventInfo request) {
        if (!hasValidFields(request)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Title and location must not be blank"));
        }
        EventInfo event = eventService.createEvent(request.getTitle(), request.getLocation());
        return ResponseEntity.status(201).body(event);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable("id") long id,
                                         @RequestBody EventInfo request) {
        if (!hasValidFields(request)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Title and location must not be blank"));
        }
        EventInfo event = eventService.updateEvent(id, request.getTitle(), request.getLocation());
        if (event == null) {
            return eventNotFound(id);
        }
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") long id) {
        if (!eventService.deleteEvent(id)) {
            return eventNotFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    private boolean hasValidFields(EventInfo event) {
        return event.getTitle() != null && !event.getTitle().isBlank()
                && event.getLocation() != null && !event.getLocation().isBlank();
    }

    private ResponseEntity<?> eventNotFound(long id) {
        return ResponseEntity.status(404)
                .body(Map.of("message", "Event with id " + id + " was not found"));
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of("applicationName", applicationName,
                "course", course, "semester", semester);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> eventById(@PathVariable("id") long id) {
        EventInfo event = eventService.getEventById(id);
        if (event == null) {
            return eventNotFound(id);
        }
        return ResponseEntity.ok(event);
    }
}
