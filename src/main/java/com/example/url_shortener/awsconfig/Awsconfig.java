package com.example.url_shortener.awsconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Arrays;
import java.util.Optional;

@Configuration
@PropertySource("classpath:application.properties")
public class Awsconfig {


//    @Bean
//    public DynamoDbClient dynamoDbClient() {
//
//        AwsCredentials credentials = ProfileCredentialsProvider.create("url-shortner-profile").resolveCredentials();
//
//        System.out.println("Profile being used: " + credentials.providerName());
//        System.out.println("AWS Profile being used: " + System.getenv("AWS_PROFILE"));
//
//
//
//        System.out.println("AWS Access Key: " + credentials.accessKeyId());
//        System.out.println("AWS Secret Key: " + credentials.secretAccessKey());
//
//
//        return DynamoDbClient.builder()
//                .region(Region.US_EAST_2)
//                .credentialsProvider(ProfileCredentialsProvider.create("url-shortner-profile"))
//                .build();
//
//    }


    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        // Check if we're running locally or on ECS
        boolean isLocal = Optional.ofNullable(System.getenv("AWS_EXECUTION_ENV")).isEmpty();

        // Use builder directly, no need for separate variable
        return DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(isLocal
                        ? ProfileCredentialsProvider.create("url-shortner-profile")
                        : DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }





    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);

        // Add the S3 bucket origin and any others you need
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200"  // For local development
             //   "http://raise-my-bucket.s3-website.us-east-2.amazonaws.com",
             //   "https://www.raisemyticket.link"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "custom-header" // Allow your custom header
        ));
        //configuration.setExposedHeaders(Arrays.asList("Authorization")); // Expose additional headers if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
