/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus.smartcampus.dao;

/**
 *
 * @author senal
 */

import com.smart_campus.smartcampus.model.Room;
import com.smart_campus.smartcampus.model.Sensor;
import com.smart_campus.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datastore {
    
    public static final Map<String, Room> rooms = new HashMap<>();
    public static final Map<String, Sensor> sensors = new HashMap<>();
    public static final Map<String, List<SensorReading>> sensorReadings = new HashMap<>();

    static {
        // Sample rooms
        Room r1 = new Room("LIB-100", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        // Sample sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-100");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LAB-101");
        Sensor s3 = new Sensor("HUM-001", "Humidity", "MAINTENANCE", 60.0, "LAB-101");
        sensors.put(s3.getId(), s3);
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);

        // Link sensors to rooms
        r2.getSensorIds().add("HUM-001");
        r1.getSensorIds().add("TEMP-001");
        r2.getSensorIds().add("CO2-001");

        // Sample readings
        List<SensorReading> readings1 = new ArrayList<>();
        readings1.add(new SensorReading(22.5));
        sensorReadings.put("TEMP-001", readings1);

        List<SensorReading> readings2 = new ArrayList<>();
        readings2.add(new SensorReading(400.0));
        sensorReadings.put("CO2-001", readings2);
        
        List<SensorReading> readings3 = new ArrayList<>();
        sensorReadings.put("HUM-001", readings3);
    }
}
