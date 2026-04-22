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
import com.smart_campus.smartcampus.exception.RoomNotEmptyException;
import com.smart_campus.smartcampus.model.Room;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
public class RoomResource {
    // Returning all the rooms in the datastore
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms(){
        List<Room> roomList = new ArrayList<>(Datastore.rooms.values());
        return Response.ok(roomList).build();
    }
    
    // Creating a new room from JSON body
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo){
        Datastore.rooms.put(room.getId(), room);
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }
    
    // Returning a single room by ID
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = Datastore.rooms.get(roomId);
        if(room == null) {
            return Response.status(404).entity("Room not found").build();
        }
        return Response.ok(room).build();
    }
    
    //Deleting a room, if it has no sensors aligned
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = Datastore.rooms.get(roomId);
        if (room == null) {
            return Response.status(404).entity("Room not found").build();
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted due to still having sensors assigned");
        }
        Datastore.rooms.remove(roomId);
        return Response.noContent().build();
    }
}
