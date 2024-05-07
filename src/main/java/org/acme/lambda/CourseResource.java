package org.acme.lambda;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.lambda.model.Course;

import java.util.List;

@Path("/api")
public class CourseResource {

    @GET()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCourses() {
        return List.of(new Course("1", "Course 1", "AD"),
                new Course("2", "Course 2", "AD"),
                new Course("3", "Course 3", "AD"));
    }

    @POST()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Course postCourse(Course course) {
        return new Course("100", course.getTitle(), course.getAuthor());
    }
}