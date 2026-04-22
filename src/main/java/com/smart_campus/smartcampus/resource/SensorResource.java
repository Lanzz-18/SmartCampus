/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus.smartcampus.resource;

/**
 *
 * @author senal
 */

import com.smart_campus.smartcampus.dao.Datastore;
import com.smart_campus.smartcampus.exception.LinkedResourceNotFoundException;
import com.smart_campus.smartcampus.model.Sensor;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
public class SensorResource {
    // If type is provided, only return sensors of that type
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(Datastore.sensors.values());
        
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor s : result) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filtered.add(s);
                }
            }
            return Response.ok(filtered).build();
        }
        
        return Response.ok(result).build();
    }
    
    @GET 
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = Datastore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("Sensor not found").build();
        }
        return Response.ok(sensor).build();
    }
    
    // Add the sensor ID to the room's sensorId list
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (!Datastore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Room with ID " + sensor.getRoomId() + " does not exist."
            );
        }
        
        Datastore.sensors.put(sensor.getId(), sensor);
        Datastore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        Datastore.sensorReadings.put(sensor.getId(), new ArrayList<>());
        
        return Response.status(201).entity(sensor).build();
    }
    
    // JAX-RS calls this method first, then routes further to that class
    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}

