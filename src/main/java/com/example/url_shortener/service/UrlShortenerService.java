package com.example.url_shortener.service;

import com.example.url_shortener.awsconfig.QRCodeGenerator;
import com.example.url_shortener.model.UrlTable;
import com.example.url_shortener.repository.UrlShortnerRepository;
import com.google.zxing.WriterException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class UrlShortenerService {

    private final UrlShortnerRepository urlShortnerRepository;


    public UrlShortenerService(UrlShortnerRepository urlShortnerRepository)
    {
        this.urlShortnerRepository= urlShortnerRepository;
    }




    private boolean isValidUrl(String url) {
        try {
            url = url.trim(); // Remove leading/trailing spaces

            URL parsedUrl = new URL(url);

            return parsedUrl.getProtocol().equalsIgnoreCase("http") || parsedUrl.getProtocol().equalsIgnoreCase("https");
        } catch (Exception e) {
            return false;
        }
    }


    public String shortenUrl(String originalUrl, String customAlias, Instant expiryDate) throws WriterException, IOException {
        // Validate URL format
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL. Only HTTP/HTTPS URLs are allowed.");
        }

        String shortUrl;
        if (customAlias != null && !customAlias.isBlank()) {
            // Validate custom alias format
            if (!customAlias.matches("^[a-zA-Z0-9_-]{3,20}$")) {
                throw new IllegalArgumentException("Custom alias must be 3-20 characters long and alphanumeric with optional '-' or '_'");
            }
            // Check if the alias already exists
            if (urlShortnerRepository.getUrlByShortUrl(customAlias) != null) {
                throw new IllegalArgumentException("Custom alias already in use. Please choose a different one.");
            }
            shortUrl = customAlias;
        } else {
            shortUrl = generateShortUrl(originalUrl); // Generate a random short URL
        }

        // Set expiration time (default to 24 hours if not provided)
        Instant expirationTime = (expiryDate != null) ? expiryDate : Instant.now().plusSeconds(86400);

        // Generate QR Code
        String qrCode = QRCodeGenerator.generateQRCode("http://short.ly/" + shortUrl, 200, 200);

        // Save URL data
        UrlTable urlTable = new UrlTable();
        urlTable.setOriginalUrl(originalUrl);
        urlTable.setShortUrl(shortUrl); // This should just be the alias, e.g., "signup123"
        urlTable.setExpiryDate(expirationTime);
        urlTable.setQrCode(qrCode);

        urlShortnerRepository.saveUrl(urlTable);
        return shortUrl; // Don't include "http://short.ly/" here; the controller will add it
    }


    private String generateShortUrl(String originalUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(originalUrl.getBytes());
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            return encoded.substring(0, 6);
        } catch (Exception e) {
            return UUID.randomUUID().toString().substring(0, 6);
        }
    }

    public UrlTable getUrlByShortUrl(String shortUrl) {
        return urlShortnerRepository.getUrlByShortUrl(shortUrl);
    }

}
