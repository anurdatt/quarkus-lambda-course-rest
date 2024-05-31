package org.acme.lambda;

import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.lambda.model.Course;
import org.acme.lambda.model.Lesson;
import org.acme.lambda.service.CourseService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<Course> getCourses(@QueryParam("courseUrl") String courseUrl) {

        if (!StringUtil.isNullOrEmpty(courseUrl)) {
            return courseService.findCoursesByCourseUrl(courseUrl);
        }


        return courseService.findAll().stream()
                .sorted(Comparator.comparing(Course::getSeqNo))
                .collect(Collectors.toList());

    }

    @GET()
    @Path("/courses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Course getCourse(@PathParam("id") String id) {
        Course course = courseService.get(Long.parseLong(id));
        return course;
    }



    @POST()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Course postCourse(Course course) {
        return courseService.add(course);
    }


    @PUT()
    @Path("/courses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Transactional()
    public Course updateCourse(@PathParam("id") String id, Course course) {
        Course updCourse = courseService.update(Long.parseLong(id), course);
        return updCourse;
    }


    @DELETE()
    @Path("/courses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteCourse(@PathParam("id") String id) {

        courseService.delete(Long.parseLong(id));
    }

}