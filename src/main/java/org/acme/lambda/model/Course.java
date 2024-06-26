package org.acme.lambda.model;


import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@RegisterForReflection
@DynamoDbBean
public class Course {
    Long id;
    String description;
    String iconUrl;
    String courseListIcon;
    String longDescription;
    String category;
    int lessonsCount;
    String courseUrl;
    int seqNo;
    int price;

    String author;

    String baseUrl;


    public Course() {
    }

    public Course(Long id, String description, String iconUrl, String courseListIcon,
                  String longDescription, String category, int lessonsCount,
                  String courseUrl, int seqNo, int price, String author, String baseUrl) {
        this.id = id;
        this.description = description;
        this.iconUrl = iconUrl;
        this.courseListIcon = courseListIcon;
        this.longDescription = longDescription;
        this.category = category;
        this.lessonsCount = lessonsCount;
        this.courseUrl = courseUrl;
        this.seqNo = seqNo;
        this.price = price;
        this.author = author;
        this.baseUrl = baseUrl;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getCourseListIcon() {
        return courseListIcon;
    }

    public void setCourseListIcon(String courseListIcon) {
        this.courseListIcon = courseListIcon;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getLessonsCount() {
        return lessonsCount;
    }

    public void setLessonsCount(int lessonsCount) {
        this.lessonsCount = lessonsCount;
    }

    public String getCourseUrl() {
        return courseUrl;
    }

    public void setCourseUrl(String courseUrl) {
        this.courseUrl = courseUrl;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
