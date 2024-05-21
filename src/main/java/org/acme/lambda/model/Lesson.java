package org.acme.lambda.model;


import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@RegisterForReflection
@DynamoDbBean
public class Lesson {
    Long id;
    String description;
    String duration;
    int seqNo;
    Long courseId;

    public Lesson() {
    }

    public Lesson(Long id, String description, String duration, int seqNo, Long courseId) {
        this.id = id;
        this.description = description;
        this.duration = duration;
        this.seqNo = seqNo;
        this.courseId = courseId;
    }



    @DynamoDbPartitionKey

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
