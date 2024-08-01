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
    String iconUrl;
    String videoUrl;
    String longDescription;
    long fileSize;
    boolean isPaid;

    public Lesson() {
    }

    public Lesson(Long id, String description, String duration, int seqNo,
                  Long courseId, String iconUrl, String videoUrl, String longDescription,
                  long fileSize, boolean isPaid) {
        this.id = id;
        this.description = description;
        this.duration = duration;
        this.seqNo = seqNo;
        this.courseId = courseId;
        this.iconUrl = iconUrl;
        this.videoUrl = videoUrl;
        this.longDescription = longDescription;
        this.fileSize = fileSize;
        this.isPaid = isPaid;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
