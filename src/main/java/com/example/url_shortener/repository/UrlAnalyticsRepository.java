package com.example.url_shortener.repository;

import com.example.url_shortener.model.UrlAnalytics;
import com.example.url_shortener.model.UrlTable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class UrlAnalyticsRepository {

    private final DynamoDbTable<UrlAnalytics> uAnalyticsTable;

    public UrlAnalyticsRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.uAnalyticsTable = dynamoDbEnhancedClient.table("UrlAnalytics", TableSchema.fromBean(UrlAnalytics.class));
    }
    public void saveAnalytics(UrlAnalytics urlAnalytics) {
        uAnalyticsTable.putItem(urlAnalytics);  // Save the analytics data to DynamoDB
    }

    public UrlAnalytics getAnalyticsByShortUrl(String shortUrl) {
        return uAnalyticsTable.getItem(r -> r.key(k -> k.partitionValue(shortUrl)));
    }



}
