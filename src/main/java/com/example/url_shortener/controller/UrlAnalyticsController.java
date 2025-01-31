package com.example.url_shortener.controller;

import com.example.url_shortener.model.UrlAnalytics;
import com.example.url_shortener.service.UrlAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
@RestController
@RequestMapping("/api/analytics")
public class UrlAnalyticsController {

    private final UrlAnalyticsService urlAnalyticsService;

    public UrlAnalyticsController(UrlAnalyticsService urlAnalyticsService) {
        this.urlAnalyticsService = urlAnalyticsService;
    }



    @PostMapping("/track")
    public void trackAnalytics(
            @RequestParam String shortUrl,
            @RequestParam String geoLocation,
            @RequestParam String deviceDetails,
            @RequestParam String referralSource) {
        urlAnalyticsService.trackAnalytics(shortUrl, geoLocation, deviceDetails, referralSource);
    }

}
