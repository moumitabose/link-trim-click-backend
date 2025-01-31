package com.example.url_shortener.repository;



import com.example.url_shortener.model.UrlTable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UrlShortnerRepository {

    private final DynamoDbTable<UrlTable> uTable;

    public UrlShortnerRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.uTable = dynamoDbEnhancedClient.table("UrlTable", TableSchema.fromBean(UrlTable.class));
    }


    public void saveUrl(UrlTable urlTable) {
        uTable.putItem(urlTable);  // This saves the UrlTable item to DynamoDB
    }

    // Retrieve a URL by its short URL (Primary Key)
    public UrlTable getUrlByShortUrl(String shortUrl) {
        return uTable.getItem(r -> r.key(k -> k.partitionValue(shortUrl)));  // Retrieve by partition key (shortUrl)
    }



}
