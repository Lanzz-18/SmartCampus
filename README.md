# SmartCampus API
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
- Apache Tomcat 9 server connected in NetBeans
- JDK 11 or higher
- Maven (bundled with NetBeans)

### Steps
1) Clone the repository:
`bashgit clone https://github.com/Lanzz-18/SmartCampus.git`
2 )Open NetBeans → File → Open Project → select the SmartCampus folder
3) Right-click the project → Clean and Build
4) Right-click the project → Run
5) Tomcat starts and the API will be available at:
`http://localhost:8080/SmartCampus/api/v1/`


## API Endpoints
- MethodEndpointDescription
- GET/api/v1/Discovery — returns API info and resource links
- GET/api/v1/rooms - Get all rooms
- POST/api/v1/rooms - Create a new room
- GET/api/v1/rooms/{roomId} - Get a single room by ID
- DELETE/api/v1/rooms/{roomId} - Delete a room (blocked if sensors exist, returns 409)
- GET/api/v1/sensors - Get all sensors
- GET/api/v1/sensors?type=CO2 - Filter sensors by type
- GET/api/v1/sensors/{sensorId} - Get a single sensor by ID
- POST/api/v1/sensors - Create a sensor (validates roomId, returns 422 if invalid)
- GET/api/v1/sensors/{sensorId}/readings - Get reading history for a sensor
- POST/api/v1/sensors/{sensorId}/readings - Add a reading (blocked if MAINTENANCE, returns 403)

## Sample curl Commands
### 1. API Discovery
Get basic information about the API version and available resources.
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/
```
### 2. Room Management
Get All Rooms
```Bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms
```
Create a New Room
```Bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "CS-201", "name": "CS Lab", "capacity": 40}'
```
Delete a Room (test) - Try deleting a room that still has sensors linked to it. This should return a 409 Conflict.
```Bash
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/LIB-100
```
### 3. Sensor Management
Get Sensors by Type
```Bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=CO2"
```
Create a Sensor Linked to a Room
```Bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-002", "type": "Temperature", "status": "ACTIVE", "currentValue": 21.0, "roomId": "LIB-100"}'
```
Log a Sensor Reading
```Bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.5}'
```
