package org.acme.lambda;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.lambda.model.Lesson;
import org.acme.lambda.service.LessonService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api")
public class LessonResource {


    @Inject
    LessonService lessonService;

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
    public List<Lesson> getLessons() {
        List<Lesson> lessons = lessonService.findAll();

        return lessons.stream()
                .sorted(Comparator.comparing(Lesson::getSeqNo))
                .collect(Collectors.toList());

    }

    @POST()
    @Path("/lessons")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Lesson postLesson(Lesson lesson) {
        return lessonService.add(lesson);
    }
}