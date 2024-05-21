package org.acme.lambda.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.lambda.model.Course;
import org.acme.lambda.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.Date;
import java.util.List;
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