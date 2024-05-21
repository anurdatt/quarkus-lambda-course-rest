package org.acme.lambda;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.lambda.model.Course;
import org.acme.lambda.service.CourseService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api")
public class CourseResource {


    @Inject
    CourseService courseService;

//    @GET()
//    @Path("/courses")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Course> getCourses() {
//        return List.of(new Course("1", "Course 1", "AD"),
//                new Course("2", "Course 2", "AD"),
//                new Course("3", "Course 3", "AD"));
//    }

    @GET()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Course> getCourses() {
        List<Course> courses = courseService.findAll();

        return courses.stream()
                .sorted(Comparator.comparing(Course::getSeqNo))
                .collect(Collectors.toList());

    }

    @POST()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Course postCourse(Course course) {
        return courseService.add(course);
    }
}