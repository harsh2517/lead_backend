package com.accountooze.controller;

import com.accountooze.service.EmailScraperService;
import com.accountooze.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/scraper")
public class ScraperController {

    @Autowired
    private EmailScraperService emailScraperService;

    @Autowired
    Utils utils;

    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return ResponseEntity.ok(emailScraperService.scrapeFromCsv(file,utils.getLoginUserId(request)));
    }
}

