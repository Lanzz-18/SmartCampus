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
import com.smart_campus.smartcampus.exception.SensorUnavailableException;
import com.smart_campus.smartcampus.model.Sensor;
import com.smart_campus.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SensorReadingResource {
    private final String sensorId;
    
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }
    
    // fetching all historical readings for a sensor
    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public Response getReadings() {
        Sensor sensor = Datastore.sensors.get(sensorId);
        if(sensor == null) {
            return Response.status(404).entity("Sensor not found").build();
        }
        
        List<SensorReading> readings = Datastore.sensorReadings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }
    
    // Updating a sensor's currentValue to match new reading
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = Datastore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(404).entity("Sensor not found").build();
        }
        
        if ("MAINTENANCE".equals(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId + " is under maintenance"
            );
        }
        
        // Auto-generate id and timestamp if not provided
        if (reading.getId() == null) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
        
        // Save the reading
        Datastore.sensorReadings.get(sensorId).add(reading);

        // Updating sensor's currentValue 
        sensor.setCurrentValue(reading.getValue());

        return Response.status(201).entity(reading).build();
    }
}
