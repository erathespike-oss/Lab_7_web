# Event Registration API — Lab 7

Name: Kamal Meiirbek  
Group: 2502DS

A Spring Boot REST API for creating, reading, updating, deleting and searching events.
Data is stored in memory and resets when the application restarts.
Lab 6 introduced read endpoints and dependency injection. Lab 7 adds CRUD operations,
title search, input checks and server-assigned IDs.

## Requirements and startup

- JDK 17 or newer. Tested with OpenJDK 21.0.12.1.
- Gradle Groovy with Gradle Wrapper 9.3.0, included in the project.
- Spring Boot 4.1.2 with Spring Web (`spring-boot-starter-webmvc`).
- The demonstration commands below use Bash and curl on Linux or macOS.

In the first terminal:

```bash
cd "$HOME/Рабочий стол/Lab_7_web"
./gradlew bootRun
```

Wait for `Tomcat started on port 8080`. Keep this terminal open.
If the application is already running, use that instance instead of starting another one.
If port 8080 is occupied by Lab 6, stop that application first.
On Windows, the startup command is `gradlew.bat bootRun`.

Open a second terminal for the requests. In Ubuntu, Ctrl+Shift+T opens a new terminal tab.
The `-i` option prints the HTTP status and response headers as well as the body.

## Teacher demonstration with curl

Run these steps in order. For predictable IDs, start with a fresh application instance
and avoid creating events from another client during the demonstration.

### 1. Read all events

```bash
curl -i http://localhost:8080/api/events
```

Expected: **200 OK**, a JSON array containing these initial events:

- ID 1: Web Design Workshop, Room 101.
- ID 2: JavaScript Quiz, Room 202.
- ID 3: IT Career Talk, Conference Hall.

Say: "GET reads the events. The controller calls the service, and Spring converts the list to JSON."

### 2. Search by title

```bash
curl -i 'http://localhost:8080/api/events?title=qUiZ'
```

Expected: **200 OK**, only JavaScript Quiz. Matching ignores case.

```bash
curl -i 'http://localhost:8080/api/events?title=NO_MATCH'
```

Expected: **200 OK** and `[]`.

Say: "RequestParam reads title from the query string. The service searches by part of the title."

### 3. Read one event and check a missing ID

```bash
curl -i http://localhost:8080/api/events/1
curl -i http://localhost:8080/api/events/999
```

Expected: **200 OK** for ID 1 and **404 Not Found** for ID 999.

Say: "PathVariable reads the event ID from the URL. A missing event produces 404."

### 4. Create an event

```bash
curl -i -X POST http://localhost:8080/api/events \
  -H 'Content-Type: application/json' \
  -d '{"title":"Spring Workshop","location":"Room 105"}'
```

Expected: **201 Created** and a new event. After a fresh start:

```json
{"id":4,"title":"Spring Workshop","location":"Room 105"}
```

JSON property order may differ.
Look at the actual ID in your response. Set the variable below to that ID;
use 4 only if your response contains ID 4:

```bash
EVENT_ID=4
```

Run the remaining commands in this same terminal so the variable stays available.

```bash
curl -i http://localhost:8080/api/events
```

Expected: **200 OK**; the list now includes the new event.

Say: "RequestBody converts the JSON into EventInfo. The service assigns the ID and adds the event."

### 5. Update both fields and verify the result

```bash
curl -i -X PUT "http://localhost:8080/api/events/$EVENT_ID" \
  -H 'Content-Type: application/json' \
  -d '{"title":"Updated Spring Workshop","location":"Room 210"}'
```

Expected: **200 OK**, the new title and location, and the same ID.

```bash
curl -i "http://localhost:8080/api/events/$EVENT_ID"
```

Expected: **200 OK** and the updated values.

Say: "PUT replaces both fields. The ID comes from the URL and stays unchanged."

### 6. Reject an invalid update

```bash
curl -i -X PUT "http://localhost:8080/api/events/$EVENT_ID" \
  -H 'Content-Type: application/json' \
  -d '{"title":"   ","location":"Room 999"}'
```

Expected: **400 Bad Request** with a clear message.

```bash
curl -i "http://localhost:8080/api/events/$EVENT_ID"
```

Expected: **200 OK**; the title is still Updated Spring Workshop and the location is still Room 210.

Say: "The controller checks the fields before calling the service, so an invalid request changes nothing."

### 7. Delete the event

```bash
curl -i -X DELETE "http://localhost:8080/api/events/$EVENT_ID"
```

Expected: **204 No Content**, with an empty response body.

```bash
curl -i "http://localhost:8080/api/events/$EVENT_ID"
```

Expected: **404 Not Found** because the event has been deleted.

Say: "DELETE removes the event. ResponseEntity sets status 204 without a response body."

### 8. Confirm that deleted IDs are not reused

```bash
curl -i -X POST http://localhost:8080/api/events \
  -H 'Content-Type: application/json' \
  -d '{"title":"After deletion","location":"Room 106"}'
```

Expected: **201 Created** with an ID greater than the deleted ID.
In a fresh demonstration, ID 4 was deleted and this event receives ID 5.

Say: "The service uses a separate increasing counter, not the list size."

### 9. Reject an invalid creation

```bash
curl -i -X POST http://localhost:8080/api/events \
  -H 'Content-Type: application/json' \
  -d '{"title":"","location":"Room 105"}'
```

Expected: **400 Bad Request**:

```json
{"message":"Title and location must not be blank"}
```

```bash
curl -i http://localhost:8080/api/events
```

Expected: **200 OK**; no event was added by the invalid request.
Missing, null, empty and whitespace-only title or location values are rejected.

### 10. Check missing IDs for update and deletion

```bash
curl -i -X PUT http://localhost:8080/api/events/999 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Missing event","location":"Room 999"}'

curl -i -X DELETE http://localhost:8080/api/events/999
```

Expected: **404 Not Found** for both requests. Neither request creates an event.
PUT validates fields before looking up the ID, so use valid fields to demonstrate this 404.

### 11. Optional PATCH check

```bash
curl -i -X PATCH http://localhost:8080/api/events/1 \
  -H 'Content-Type: application/json' \
  -d '{"location":"Room 300"}'
```

Expected: **405 Method Not Allowed**. PATCH is not implemented and is not required for this lab.
Our PUT endpoint requires both fields; it does not perform a partial update.

### 12. Demonstrate the reset after restart

In the first terminal, press Ctrl+C to stop the application, then run:

```bash
./gradlew bootRun
```

Wait for the server to start. In the second terminal:

```bash
curl -i http://localhost:8080/api/events
```

Expected: **200 OK** with only the original three events, IDs 1, 2 and 3.
The counter also starts again at 4.

Say: "The events exist only in memory. Restarting creates the initial list again."

## Quick reference

| Method | Path | Expected behavior |
| --- | --- | --- |
| GET | /api/events | 200, all events |
| GET | /api/events?title=quiz | 200, matching events or an empty array |
| GET | /api/events/{id} | 200 or 404 |
| POST | /api/events | 201 or 400 |
| PUT | /api/events/{id} | 200, 400 or 404 |
| DELETE | /api/events/{id} | 204 with no body, or 404 |
| GET | /api/hello | 200, welcome message from Lab 6 |
| GET | /api/info | 200, application settings from Lab 6 |

curl options:

- `-i`: show the HTTP status and headers.
- `-X`: select the HTTP method.
- `-H`: add a request header.
- `-d`: send the request body.

## Short explanation for the defense

- EventInfo contains id, title and location.
- EventService stores an ArrayList and an increasing nextId counter. It performs searches and changes the list.
- RegistrationController reads requests, checks fields, calls the service and selects HTTP statuses.
- Spring creates EventService through @Service and injects it into the controller constructor.
- @PathVariable reads an ID from the URL path.
- @RequestParam reads a query parameter such as title=quiz.
- @RequestBody converts JSON request data into a Java object.
- ResponseEntity controls the response status, headers and body.
- @RestController allows returned data to be written to the response; the configured JSON converter serializes our objects and lists.

## Previous verification

The implementation was checked with 59 HTTP requests and three checks after restart.
Checks covered CRUD, search, invalid fields, missing IDs, ID preservation, and increasing IDs after deletion.
POST and PUT each passed nine invalid-field cases without changing the list.
The check log is in docs/endpoint-checks.txt. No unit tests were added.
The optional PATCH command above is provided for manual demonstration; it was not part of that recorded test run.

## Build and GitHub submission

```bash
./gradlew build
```

The Gradle build was verified on JDK 21. The project includes Java sources, resources,
Gradle files and the Wrapper. .gitignore excludes .gradle, build and local IDE settings.

To upload to a new empty GitHub repository, replace YOUR_USERNAME and run these commands yourself:

```bash
git init
git add .
git commit -m "Complete Spring Boot lab 7"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/event-registration-api-lab7.git
git push -u origin main
```
