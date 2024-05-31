package org.acme.lambda.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
public class LessonService {

    private DynamoDbTable<Lesson> lessonTable;

    Logger logger = LoggerFactory.getLogger(LessonService.class);


    @Inject
    public LessonService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        lessonTable = enhancedClient.table("Lesson", TableSchema.fromBean(Lesson.class));
    }


    public List<Lesson> findAll() {
        return lessonTable.scan().items().stream().collect(Collectors.toList());
    }


    public List<Lesson> findByCourseId(Long courseId) {
        return lessonTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("courseId = :courseId")
                                .expressionValues(Map.of(":courseId", AttributeValue.builder()
                                        .n(String.valueOf(courseId))
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }


    public Lesson get(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return lessonTable.getItem(partitionKey);
    }

    public Lesson update(Long id, Lesson lesson) {
        lesson.setId(id);
        UpdateItemEnhancedRequest request = UpdateItemEnhancedRequest
                .builder(Lesson.class)
                .ignoreNulls(true).item(lesson).build();
        return lessonTable.updateItem(request);
    }

    public Lesson add(Lesson lesson) {
//        String id = UUID.randomUUID().toString();
        Long did = new Date().getTime();

        lesson.setId(did);
        lessonTable.putItem(lesson);
        return lesson;
    }

    public Lesson delete(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return lessonTable.deleteItem(partitionKey);
    }
}