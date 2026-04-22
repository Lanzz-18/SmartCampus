package com.smart_campus.smartcampus.exception;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author senal
 */

import com.smart_campus.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException>{
    @Override
    public Response toResponse(RoomNotEmptyException e){
        ErrorMessage error = new ErrorMessage(
            e.getMessage(),
            409,
            "http://localhost:8080/SmartCampus/api/v1/docs"
        );
        return Response.status(409).entity(error).build();
    }
}
