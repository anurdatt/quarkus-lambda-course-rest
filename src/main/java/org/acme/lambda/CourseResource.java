package org.acme.lambda;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api")
public class CourseResource {

    @GET()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> posts() {
        return List.of("Course 1", "Course 2", "Course 3");
    }
}