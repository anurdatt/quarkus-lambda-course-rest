package org.acme.lambda.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.lambda.model.Tag;
import org.acme.lambda.util.DDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class TagService {

    private DynamoDbTable<Tag> tagTable;

    Logger logger = LoggerFactory.getLogger(TagService.class);


    @Inject
    public TagService(DDBUtil ddbUtil) {
        DynamoDbEnhancedClient enhancedClient = ddbUtil.getEnhancedDDBClient();
        tagTable = enhancedClient.table("Tag", TableSchema.fromBean(Tag.class));
    }


    public List<Tag> findAll() {
        return tagTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<Tag> findTagsByTagUrl(String tagUrl) {

        return tagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("tagUrl = :tagUrl")
                                .expressionValues(Map.of(":tagUrl", AttributeValue.builder()
                                        .s(tagUrl)
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }

    public List<Tag> findBySourceApp(String sourceApp) {
        return tagTable.scan(s -> s
                        .consistentRead(true)
                        .filterExpression(Expression.builder()
                                .expression("sourceApp = :sourceApp")
                                .expressionValues(Map.of(":sourceApp", AttributeValue.builder()
                                        .s(sourceApp)
                                        .build()))
                                .build()))
                .items().stream().collect(Collectors.toList());
    }
    public Tag get(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return tagTable.getItem(partitionKey);
    }

    public Tag update(Long id, Tag tag) {
        tag.setId(id);
        UpdateItemEnhancedRequest request = UpdateItemEnhancedRequest
                .builder(Tag.class)
                .ignoreNulls(true).item(tag).build();
        return tagTable.updateItem(request);
    }

    public Tag add(Tag tag) {
//        String id = UUID.randomUUID().toString();
        Long did = new Date().getTime();
        String tid = tag.getName()
//                .replaceAll("[!@#'$%^&*]", "")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll(" ", "-")
                .toLowerCase(Locale.ROOT);
        Long rid = Math.round(Math.random() * 100);
        tag.setId(did);
        tag.setTagUrl(tid + "-" + rid);
        tagTable.putItem(tag);
        return tag;
    }

    public Tag addWithSourceApp(Tag tag, String sourceApp) {
        tag.setSourceApp(sourceApp);
        return add(tag);
    }

    public Tag delete(Long id) {
        Key partitionKey = Key.builder().partitionValue(id).build();
        return tagTable.deleteItem(partitionKey);
    }
}