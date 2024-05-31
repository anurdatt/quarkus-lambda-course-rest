package org.acme.lambda;

import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.lambda.model.Course;
import org.acme.lambda.model.Lesson;
import org.acme.lambda.service.CourseService;
import org.acme.lambda.service.LessonService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api")
public class LessonResource {


    @Inject
    LessonService lessonService;

    @Inject
    CourseService courseService;

//    @GET()
//    @Path("/lessons")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Lesson> getLessons() {
//        return List.of(new Lesson("1", "Lesson 1", "AD"),
//                new Lesson("2", "Lesson 2", "AD"),
//                new Lesson("3", "Lesson 3", "AD"));
//    }

    @GET()
    @Path("/lessons")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Lesson> getLessons(@QueryParam("courseUrl") String courseUrl) {
        List<Lesson> lessons;
        if (!StringUtil.isNullOrEmpty(courseUrl)) {
            List<Course> courses = courseService.findCoursesByCourseUrl(courseUrl);
            if (courses != null && courses.size() > 0) {
                lessons = lessonService.findByCourseId(courses.get(0).getId());
            } else {
                return new ArrayList<>();
            }
        } else {
            lessons = lessonService.findAll();
        }

        return lessons.stream()
                .sorted(Comparator.comparing(Lesson::getSeqNo))
                .collect(Collectors.toList());

    }

    @GET()
    @Path("/lessons/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Lesson getLesson(@PathParam("id") String id) {
        Lesson lesson = lessonService.get(Long.parseLong(id));
        return lesson;
    }


    @POST()
    @Path("/lessons")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Lesson postLesson(Lesson lesson) {
        return lessonService.add(lesson);
    }


    @PUT()
    @Path("/lessons/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Transactional()
    public Lesson updateLesson(@PathParam("id") String id, Lesson lesson) {
        Lesson updLesson = lessonService.update(Long.parseLong(id), lesson);
        return updLesson;
    }


    @DELETE()
    @Path("/lessons/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteLesson(@PathParam("id") String id) {

        lessonService.delete(Long.parseLong(id));
    }

}