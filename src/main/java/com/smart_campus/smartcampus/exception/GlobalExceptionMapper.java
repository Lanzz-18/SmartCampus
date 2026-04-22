/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smart_campus.smartcampus.exception;

/**
 *
 * @author senal
 */

import com.smart_campus.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable e) {
        ErrorMessage error = new ErrorMessage(
            e.getMessage() != null ? e.getMessage() : "Error occurred",
            500,
            "http://localhost:8080/SmartCampus/api/v1/docs"
        );
        return Response.status(500).entity(error).build();
    }
}
