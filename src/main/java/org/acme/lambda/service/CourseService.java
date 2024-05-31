package org.acme.lambda.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.lambda.model.Course;
import org.acme.lambda.model.Lesson;
import org.acme.lambda.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CourseService {

    private DynamoDbTable<Course> courseTable;

    Logger logger = LoggerFactory.getLogger(CourseService.class);


    @Inject
    public CourseService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
    }


    public List<Course> findAll() {
        return courseTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Course> findCoursesByCourseUrl(String courseUrl) {

        return courseTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("courseUrl = :courseUrl")
                                .expressionValues(Map.of(":courseUrl", AttributeValue.builder()
                                        .s(courseUrl)
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public Course get(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return courseTable.getItem(partitionKey);
    }

    public Course update(Long id, Course course) {
        course.setId(id);
        UpdateItemEnhancedRequest request = UpdateItemEnhancedRequest
                .builder(Course.class)
                .ignoreNulls(true).item(course).build();
        return courseTable.updateItem(request);
    }

    public Course add(Course course) {
//        String id = UUID.randomUUID().toString();
        Long did = new Date().getTime();

        course.setId(did);
        courseTable.putItem(course);
        return course;
    }

    public Course delete(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return courseTable.deleteItem(partitionKey);
    }
}