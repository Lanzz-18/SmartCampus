# SmartCampus API
| Name | L. A. D. S. K. Perera |
|--------|----------|
| UoW | w2120001 |
| IIT | 20231386 |
| Group | CS-17 |

A RESTful API built with JAX-RS (Jersey) for the 5COSC022W Client-Server Architectures coursework at University of Westminster. This API manages rooms and sensors across a university campus. Users can create rooms, register sensors, log readings, and the error handling makes sure nothing crashes.

## Tech Stack

- Java EE 8
- JAX-RS / Jersey 2.47
- Jackson (JSON serialisation)
- Apache Tomcat 9
- Maven

## How to Build and Run
### Prerequisites
- Apache NetBeans (with Java Web and EE plugin active)
- Apache Tomcat 9 (download from https://tomcat.apache.org/download-90.cgi and register in NetBeans under Tools → Servers)
- When registering Tomcat in NetBeans (Tools → Servers → Add Server -> Apache Tomcat) -> Select Tomcat install folder -> Start a TomCat server
- JDK 11 or higher
- Maven (bundled with NetBeans)

### Steps
1) Clone the repository:
```bash
git clone https://github.com/Lanzz-18/SmartCampus.git
```
2) Open NetBeans → File → Open Project → navigate to the cloned folder → select it (the folder containing pom.xml, "SmartCampus-main")
3) If you see a double folder like SmartCampus-main/SmartCampus-main, choose the inner one that contains pom.xml
4) Right-click the project → Clean and Build
5) Right-click the project → Run
6) Tomcat starts and the API will be available at:
`http://localhost:8080/SmartCampus/api/v1/`


## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1/ | Discovery — returns API info and resource links |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{roomId} | Get a single room by ID |
| DELETE | /api/v1/rooms/{roomId} | Delete a room (blocked if sensors exist, 409) |
| GET | /api/v1/sensors | Get all sensors |
| GET | /api/v1/sensors?type=CO2 | Filter sensors by type |
| GET | /api/v1/sensors/{sensorId} | Get a single sensor by ID |
| POST | /api/v1/sensors | Create a sensor (validates roomId, 422 if invalid) |
| GET | /api/v1/sensors/{sensorId}/readings | Get reading history for a sensor |
| POST | /api/v1/sensors/{sensorId}/readings | Add a reading (blocked if MAINTENANCE, 403) |

---

## Sample curl Commands
**1. API Discovery**
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/
```

**2. Get all rooms**
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms
```

**3. Create a new room**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "CS-201", "name": "CS Lab", "capacity": 40}'
```

**4. Delete a room that still has sensors (expect 409)**
```bash
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/LIB-100
```

**5. Get sensors filtered by type**
```bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=CO2"
```

**6. Create a sensor linked to a room**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-002", "type": "Temperature", "status": "ACTIVE", "currentValue": 21.0, "roomId": "LIB-100"}'
```

**7. Log a sensor reading**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.5}'
```


## Report Answers 
### Part 1
#### Question 1
JAX-RS creates a brand new instance of each resource class for every single incoming request. This is called a per-request lifecycle. Every time someone calls GET /rooms, JAX-RS creates a fresh RoomResource object to handle it, then it destroys the object once the response is sent. This causes a problem with in-memory data storage. If the data was stored as a regular field inside the resource class, it would be wiped out after every request since the object gets destroyed. To fix this, I stored all data in Datastore.java as static fields. Static fields belong to the class itself rather than any individual instance, so they stay in memory for as long as the server is running regardless of how many resource instances get created and destroyed.

#### Question 2
HATEOAS stands for Hypermedia as the Engine of Application State. Basically the idea is that API responses should include links to related resources and actions rather than just returning raw data. For example, a response listing rooms could also include links to each room's sensors. This is considered advanced REST design because it makes the API self-documenting. Instead of a client developer having to memorise every endpoint or constantly check documentation, they can check the links embedded in the responses. If the API changes its URL structure, clients that navigate via links automatically adapt rather than breaking because they have a hardcoded URL. In the discovery endpoint at GET /api/v1, it return links to /api/v1/rooms and /api/v1/sensors so a client immediately knows where to go without needing any external documentation. 


### Part 2
#### Question 1
If you return only IDs in the room list, the client has to make a separate GET request for every single room to get its full details. For 100 rooms that means 101 network requests total, which creates a lot of unnecessary traffic and significantly increases the time a user waits for a response.
Returning full room objects in the list response means one single request gets everything the client needs. The downside is a larger response payload, but for most practical cases this is worth it since the room model is small. The client also does not need to do any extra processing or make follow-up requests to display the data. For this particular API, it returns full room objects because the data model is lightweight and it keeps the client implementation simple.

#### Question 2
The DELETE operation is partially idempotent in this implementation. Idempotency means that sending the same request multiple times produces the same result as sending it once. In this case, if you send DELETE /rooms/EMPTY-101 the first time and the room exists with no sensors, it gets deleted and returns 204 No Content. If you send it again, the room no longer exists so the server returns 404 Not Found. The status codes are different between the first and second call. However, the server state remains the same after the first successful delete. Calling DELETE again does not cause any new side effects or data corruption because the room is still gone.

### Part 3
#### Question 1
When a POST method is annotated with @Consumes(MediaType.APPLICATION_JSON), it tells JAX-RS to only accept requests where the Content-Type header is set to application/json. If a client sends a request with Content-Type such as text/plain or application/xml, JAX-RS rejects the request immediately before the resource method even runs. The resource method code is never executed so there is no risk of incorrectly formatted data getting processed or stored. This is JAX-RS performing content negotiation, it checks the incoming content type and only routes the request to the method if the format matches what the method declared it can handle. 

#### Question 2
Using a query parameter such as “GET /sensors?type=CO2” is generally better than embedding the filter in the URL path like “GET /sensors/type/CO2” for multiple reasons. First, query parameters are naturally optional. If you leave out “?type=CO2” you just get all sensors. With a path-based approach you would need a completely separate endpoint to handle the unfiltered case. 
Second, the URL structure clearly communicates intent. “/sensors?type=CO2” signals that this is a search or filter operation on the sensors collection. “/sensors/type/CO2” looks like it is trying to access a sub-resource called type which is confusing and could conflict with other path parameters. 

### Part 4
#### Question 1
The sub-resource locator pattern means that instead of defining every nested endpoint in one large class, you delegate it all to a separate dedicated class. In this particular implementation, SensorResource has a method annotated with @Path("/{sensorId}/readings") that does not handle GET or POST itself, instead it returns a new instance of SensorReadingResource with the sensorId passed in. 
JAX-RS then routes the rest of the request to that class. This keeps code organised and manageable. If every endpoint was defined in one class, SensorResource would become very long and difficult to read and would have too many things mixed together. By splitting into separate classes each class has one clear responsibility and makes the code easier to maintain.

### Part 5
#### Question 1
When a client POST’s a new sensor with a roomId that does not exist in the system, returning 404 Not Found is misleading. The URL being requested, “/api/v1/sensors”, does exist and the server found it without any problem. The issue is inside the content of the request body. HTTP 422 Unprocessable Entity is more semantically accurate because it means the server understood the request, the JSON was syntactically valid, but the content of the payload does not make sense in the current context. In this case the roomId references a room that does not exist, which is a semantic error in the data rather than a missing resource at the URL level. 

#### Question 2
Exposing an internal Java stack trace to API consumers is a significant security risk for several reasons. First, it reveals internal code structure. A stack trace shows the full class names, package names, and method names of the codebase. An attacker can view how the application is organised internally which helps them identify targets.
Second, it reveals library and framework versions. If any of those versions have known security vulnerabilities, the attacker knows exactly which exploit to attempt. As well as information like server and file system information such as the Java version, Tomcat version, and sometimes absolute file paths on the server can be revealed. All of this helps an attacker plan out a proper attack on the system. The GlobalExceptionMapper in this implementation catches any unexpected Throwable and returns a generic message saying An unexpected error occurred.

#### Question 3
If logging was added manually by inserting Logger.info() calls inside every resource method, the same logging code would need to be copied into every single method across RoomResource, SensorResource, and SensorReadingResource. That is a lot of repeated code and is not good practice. Moreover, if the log format needs to be changed every single method would need to be updated individually. A JAX-RS filter solves this by intercepting every request and response automatically in one single place. LoggingFilter runs for every API call without touching any resource class at all. This follows the DRY principle which means Don't Repeat Yourself. Filters also keep resource classes cleaner and focused on their actual job. In this particular implementation, RoomResource handles rooms and SensorResource handles sensors.

