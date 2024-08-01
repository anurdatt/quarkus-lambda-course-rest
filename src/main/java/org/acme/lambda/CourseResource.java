package org.acme.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.amazon.lambda.http.model.AwsProxyRequestContext;
import io.quarkus.runtime.util.StringUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.acme.lambda.model.Course;
import org.acme.lambda.model.CourseTag;
import org.acme.lambda.model.Lesson;
import org.acme.lambda.model.Tag;
import org.acme.lambda.service.CourseService;
import org.acme.lambda.service.CourseTagService;
import org.acme.lambda.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/api")
public class CourseResource {


    Logger logger = LoggerFactory.getLogger(CourseResource.class);

    @Inject
    CourseService courseService;

    @Inject
    TagService tagService;

    @Inject
    CourseTagService courseTagService;

    public static class CourseWithTags {
        Course course;
        List<Tag> tags;

        public CourseWithTags() {
        }

        public CourseWithTags(Course course, List<Tag> tags) {
            this.course = course;
            this.tags = tags;
        }

        public Course getCourse() {
            return course;
        }

        public void setCourse(Course course) {
            this.course = course;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }

//    @GET()
//    @Path("/courses")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Course> getCourses(@QueryParam("courseUrl") String courseUrl) {
//
//        if (!StringUtil.isNullOrEmpty(courseUrl)) {
//            return courseService.findCoursesByCourseUrl(courseUrl);
//        }
//
//
//        return courseService.findAll().stream()
//                .sorted(Comparator.comparing(Course::getSeqNo))
//                .collect(Collectors.toList());
//
//    }

    @GET()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CourseWithTags> getCourses(@QueryParam("courseUrl") String courseUrl,
                                           @QueryParam("tagUrl") String tagUrl) {

        List<Course> courses;
        if (!StringUtil.isNullOrEmpty(courseUrl)) {
            courses = courseService.findCoursesByCourseUrl(courseUrl);
        } else if (!StringUtil.isNullOrEmpty(tagUrl)) {

            courses = tagService.findTagsByTagUrl(tagUrl)
                    .stream()
                    .map(Tag::getId)
                    .flatMap((tagId) -> courseTagService.findByTagId(tagId).stream())
                    .map(CourseTag::getCourseId).distinct()
                    .map((courseId) -> courseService.get(courseId))
                    .collect(Collectors.toList());
        } else {
            courses = courseService.findAll();
        }
        return courses
                .stream()
                .map(c -> new CourseWithTags(c, courseTagService.findByCourseId(c.getId())
                        .stream()
                        .map(CourseTag::getTagId)
                        .map(tid -> tagService.get(tid))
                        .collect(Collectors.toList()))
                ).collect(Collectors.toList());

    }

    @GET()
    @Path("/courses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Course getCourse(@PathParam("id") String id) {
        Course course = courseService.get(Long.parseLong(id));
        return course;
    }

    @GET()
    @Path("/courses/{id}/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public CourseWithTags getCourseDetail(@PathParam("id") String id) {
        Course course = courseService.get(Long.parseLong(id));

        List<CourseTag> courseTags = courseTagService.findByCourseId(Long.parseLong(id));

        List<Long> tagIds = courseTags.stream().map(CourseTag::getTagId).collect(Collectors.toList());

        List<Tag> tags = tagIds.stream().map(tagId -> tagService.get(tagId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new CourseWithTags(course, tags);
    }



//    @POST()
//    @Path("/courses")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Course addCourse(Course course) {
//        return courseService.add(course);
//    }


    @POST()
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional()
    public CourseWithTags addCourse(CourseWithTags courseWithTags, @Context AwsProxyRequestContext req) {
        String userId = "";
        if (req != null) {
            try {
                logger.info("Proxy request context = {}", new ObjectMapper().writeValueAsString(req));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
//            userId = req.getAuthorizer().getContextValue("userId");
        }
        logger.info("Received in request context, userId = {}", userId);
//        String username = "anuran.datta@hotmail.com"; //Hardcoded dummy
//        if (!StringUtil.isNullOrEmpty(userId)) {
//            username = userId;
//        }
//        note.setUsername(username);
        Course course = courseService.add(courseWithTags.course);
        courseWithTags.tags
                .stream()
                .forEachOrdered(tag ->
                        courseTagService.add(new CourseTag(course.getId(), tag.getId())));

        return new CourseWithTags(course, courseWithTags.tags);
    }


//    @PUT()
//    @Path("/courses/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
////    @Transactional()
//    public Course updateCourse(@PathParam("id") String id, Course course) {
//        Course updCourse = courseService.update(Long.parseLong(id), course);
//        return updCourse;
//    }



    @PUT()
    @Path("/courses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional()
    public CourseWithTags updateCourse(@PathParam("id") String id, CourseWithTags courseWithTags) {
        Course course = courseService.update(Long.parseLong(id), courseWithTags.course);
        courseWithTags.tags.stream().forEachOrdered(tag -> {
            if (courseTagService.findByCourseIdAndTagId(course.getId(), tag.getId()).size() == 0) {
                courseTagService.add(new CourseTag(course.getId(), tag.getId()));
            }
        });
        courseTagService.findByCourseId(course.getId()).stream().forEachOrdered(courseTag -> {
            if(!courseWithTags.tags.stream().anyMatch(tag -> tag.getId().equals(courseTag.getTagId()))) {
                logger.info("Going to DELETE <" + courseTag.getTagId() + ">");
                courseTagService.delete(courseTag.getId());
            }
        });
        return new CourseWithTags(courseWithTags.course, courseWithTags.tags);
    }



//    @DELETE()
//    @Path("/courses/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public void deleteCourse(@PathParam("id") String id) {
//
//        courseService.delete(Long.parseLong(id));
//    }

    @DELETE()
    @Path("/courses/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional()
    public void deleteCourse(@PathParam("id") String id) {
        courseTagService.deleteByCourseId(Long.parseLong(id));
        courseService.delete(Long.parseLong(id));
    }

}