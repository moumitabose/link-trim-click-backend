package com.example.url_shortener.service;

import com.example.url_shortener.model.UrlAnalytics;
import com.example.url_shortener.repository.UrlAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class UrlAnalyticsService {


    private final UrlAnalyticsRepository urlAnalyticsRepository;

    public UrlAnalyticsService(UrlAnalyticsRepository urlAnalyticsRepository) {
        this.urlAnalyticsRepository = urlAnalyticsRepository;
    }

    public String convertDateToString(Date date) {
        Instant instant = date.toInstant();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
        return formatter.format(instant);
    }



    public void trackAnalytics(String shortUrl, String geoLocation, String deviceDetails, String referralSource) {
        UrlAnalytics analytics = new UrlAnalytics();
        analytics.setShortUrl(shortUrl);
        Date date = new Date();  // Current timestamp
        String timestampStr = convertDateToString(date);
        analytics.setTimestamp(timestampStr);  // Assuming analytics.setTimestamp accepts a String
        analytics.setClickCount(1); // Increment based on the click count logic
        analytics.setGeoLocation(geoLocation);
        analytics.setDeviceDetails(deviceDetails);
        analytics.setReferralSource(referralSource);

        urlAnalyticsRepository.saveAnalytics(analytics);
    }
}
