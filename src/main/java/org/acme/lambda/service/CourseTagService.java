package org.acme.lambda.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.lambda.model.CourseTag;
import org.acme.lambda.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CourseTagService {

    private DynamoDbTable<CourseTag> courseTagTable;

    Logger logger = LoggerFactory.getLogger(CourseTagService.class);


    @Inject
    public CourseTagService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        courseTagTable = enhancedClient.table("CourseTag", TableSchema.fromBean(CourseTag.class));
    }


    public List<CourseTag> findAll() {
        return courseTagTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<CourseTag> findByCourseId(Long courseId) {
        return courseTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("courseId = :courseId")
                                .expressionValues(Map.of(":courseId", AttributeValue.builder()
                                        .n(String.valueOf(courseId))
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public List<CourseTag> findByTagId(Long tagId) {
        return courseTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("tagId = :tagId")
                                .expressionValues(Map.of(":tagId", AttributeValue.builder()
                                        .n(String.valueOf(tagId))
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public List<CourseTag> findByCourseIdAndTagId(Long courseId, Long tagId) {
        return courseTagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("courseId = :courseId and tagId = :tagId")
                                .expressionValues(Map.of(":courseId", AttributeValue.builder().n(String.valueOf(courseId)).build(),
                                        ":tagId", AttributeValue.builder().n(String.valueOf(tagId)).build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public CourseTag add(CourseTag courseTag) {
//        int pinx = courseTag.getCourseId().lastIndexOf("-");
//        String pidInclude = courseTag.getCourseId();
//        if(pinx > 0) {
//            pidInclude = courseTag.getCourseId().substring(0, pinx);
//        }
//        int tinx = courseTag.getTagId().lastIndexOf("-");
//        String tidInclude = courseTag.getTagId();
//        if(tinx > 0) {
//            tidInclude = courseTag.getTagId().substring(0, tinx);
//        }
        Long did = new Date().getTime();
//        courseTag.setId(pidInclude + "-" + tidInclude + "-" + did);
        courseTag.setId(did);
        courseTagTable.putItem(courseTag);
        return courseTag;
    }

//    public CourseTag deleteByCourseId(Long courseId) {
//        return courseTagTable.deleteItem(d -> d
//                .conditionExpression(Expression.builder()
//                        .expression("courseId = :courseId")
//                        .expressionValues(Map.of(":courseId", AttributeValue.builder()
//                                .n(String.valueOf(courseId)).build()))
//                        .build()));
//    }

    public List<CourseTag> deleteByCourseId(Long courseId) {
        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("#cid = :cid")
                        .expressionNames(Map.of("#cid", "courseId"))
                        .expressionValues(Map.of(":cid", AttributeValue.builder().n(String.valueOf(courseId)).build()))
                        .build())
                .build();

        List<CourseTag> courseTags = courseTagTable.scan(scanEnhancedRequest).items()
                .stream().collect(Collectors.toList());


        courseTags.stream().forEachOrdered(pt -> courseTagTable.deleteItem(pt));

        return courseTags;
    }

    public List<CourseTag> deleteByTagId(Long tagId) {

        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("#tid = :tid")
                        .expressionNames(Map.of("#tid", "tagId"))
                        .expressionValues(Map.of(":tid", AttributeValue.builder().n(String.valueOf(tagId)).build()))
                        .build())
                .build();

        List<CourseTag> courseTags = courseTagTable.scan(scanEnhancedRequest).items()
                .stream().collect(Collectors.toList());


        courseTags.stream().forEachOrdered(pt -> courseTagTable.deleteItem(pt));

        return courseTags;
    }

    public CourseTag delete(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return courseTagTable.deleteItem(partitionKey);
    }


}