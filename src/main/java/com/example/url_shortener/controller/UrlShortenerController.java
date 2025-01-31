package com.example.url_shortener.controller;

import com.example.url_shortener.model.UrlResponse;
import com.example.url_shortener.model.UrlTable;
import com.example.url_shortener.service.UrlShortenerService;
import com.google.zxing.WriterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
public class UrlShortenerController {


    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService)
    {
        this.urlShortenerService=urlShortenerService;

    }

    @PostMapping("/shorten")
    public ResponseEntity<Object> shortenUrl(@RequestBody UrlTable urlTable) throws Exception {
        // Shorten the URL and generate QR code
        String shortUrl = urlShortenerService.shortenUrl(
                urlTable.getOriginalUrl(),
                urlTable.getCustomAlias(),
                urlTable.getExpiryDate()
        );

        // Retrieve the saved entity from the database using shortUrl
        UrlTable savedUrl = urlShortenerService.getUrlByShortUrl(shortUrl);

        if (savedUrl == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving QR code");
        }

        // Prepare response object
        UrlResponse response = new UrlResponse();
        response.setShortUrl("http://short.ly/" + shortUrl); // Base URL
        response.setQrCode(savedUrl.getQrCode());  // Retrieve QR code from the saved entity

        // Include expiry date if available
        if (savedUrl.getExpiryDate() != null) {
            response.setExpiresOn(savedUrl.getExpiryDate().toString());
        }

        return ResponseEntity.ok(response);
    }

//
//    139/86
//
//    pulse 78





    @GetMapping("/{shortUrl}")
    public ResponseEntity<UrlResponse> getOriginalUrl(@PathVariable String shortUrl) {
        UrlTable urlTable = urlShortenerService.getUrlByShortUrl(shortUrl);

        if (urlTable != null) {
            // Prepare the response object
            UrlResponse response = new UrlResponse();
            response.setShortUrl("http://short.ly/" + shortUrl);  // Include the base URL
            response.setShortUrl(urlTable.getOriginalUrl());
            response.setQrCode(urlTable.getQrCode());  // Include QR code
            response.setExpiresOn(urlTable.getExpiryDate() != null ? urlTable.getExpiryDate().toString() : null);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
