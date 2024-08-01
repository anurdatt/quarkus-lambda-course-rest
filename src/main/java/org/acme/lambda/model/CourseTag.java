package org.acme.lambda.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@RegisterForReflection
@DynamoDbBean
public class CourseTag {
    private Long id;
    private Long courseId;
    private Long tagId;

    public CourseTag() {
    }

    public CourseTag(Long id, Long courseId, Long tagId) {
        this.id = id;
        this.courseId = courseId;
        this.tagId = tagId;
    }

    public CourseTag(Long courseId, Long tagId) {
        this.courseId = courseId;
        this.tagId = tagId;
    }

    @DynamoDbPartitionKey
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
