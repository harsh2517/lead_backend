package com.accountooze.service;

import com.accountooze.model.ScrapedEmail;
import com.accountooze.repo.ScrapedEmailRepository;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailScraperService {

    @Autowired
    private ScrapedEmailRepository scrapedEmailRepository;

    public Map<String, Object> scrapeFromCsv(MultipartFile file, Long userId) {
        int totalWebsites = 0;
        int totalEmails = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            while ((line = br.readLine()) != null) {
                String website = line.trim();

                if (website.isEmpty()) continue;
                if (website.equalsIgnoreCase("website")) continue;

                totalWebsites++;

                if (!website.startsWith("http://") && !website.startsWith("https://")) {
                    website = "https://" + website;
                }

                try {
                    Set<String> emails = scrapeEmailsFromWebsite(website);

                    for (String email : emails) {
                        if (!scrapedEmailRepository.existsByEmailAndWebsite(email, website)) {
                            ScrapedEmail scrapedEmail = new ScrapedEmail();
                            scrapedEmail.setWebsite(website);
                            scrapedEmail.setEmail(email);
                            scrapedEmail.setUserId(Math.toIntExact(userId));

                            scrapedEmailRepository.save(scrapedEmail);
                            totalEmails++;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Scrape failed: " + website + " => " + e.getMessage());
                }


            }

        } catch (Exception e) {
            throw new RuntimeException("CSV read failed: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Scraping completed");
        response.put("totalWebsites", totalWebsites);
        response.put("totalEmailsSaved", totalEmails);

        return response;
    }

    private boolean isInvalidEmail(String email) {


        return email.contains("sentry") ||
                email.contains("noreply") ||
                email.contains("no-reply") ||
                email.contains("donotreply") ||
                email.contains("example.com") ||
                email.contains("test") ||
                email.contains("dummy") ||
                email.contains("wixpress") ||
                email.contains("cloudflare") ||
                email.contains("amazonaws") ||
                email.contains("localhost") ||
                email.contains(".png") ||
                email.contains(".jpg");
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );

    private Set<String> scrapeEmailsFromWebsite(String website) {
        Set<String> emails = new HashSet<>();
        Set<String> pagesToCheck = new HashSet<>();

        pagesToCheck.add(website);
        pagesToCheck.add(website + "/contact");
        pagesToCheck.add(website + "/contact-us");
        pagesToCheck.add(website + "/about");
        pagesToCheck.add(website + "/about-us");

        for (String url : pagesToCheck) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .followRedirects(true)
                        .get();

                // 1. HTML mathi email
                extractEmails(doc.html(), emails);

                // 2. Text mathi email
                extractEmails(doc.text(), emails);

                // 3. mailto links mathi email
                doc.select("a[href^=mailto:]").forEach(link -> {
                    String href = link.attr("href")
                            .replace("mailto:", "")
                            .split("\\?")[0]
                            .trim()
                            .toLowerCase();

                    extractEmails(href, emails);
                });

            } catch (Exception e) {
                System.out.println("Scrape failed page: " + url + " => " + e.getMessage());
            }
        }

        return emails;
    }

    private void extractEmails(String content, Set<String> emails) {
        Matcher matcher = EMAIL_PATTERN.matcher(content);

        while (matcher.find()) {
            String email = matcher.group().toLowerCase().trim();

            if (isInvalidEmail(email)) continue;

            emails.add(email);
        }
    }

    private Set<String> buildCandidateBaseUrls(String rawWebsite) {
        Set<String> urls = new HashSet<>();

        String site = rawWebsite.trim();
        site = site.replaceAll("/+$", "");

        if (site.startsWith("http://") || site.startsWith("https://")) {
            urls.add(site);

            if (site.contains("://www.")) {
                urls.add(site.replace("://www.", "://"));
            }

            if (site.startsWith("https://")) {
                urls.add(site.replace("https://", "http://"));
            }
        } else {
            urls.add("https://" + site);
            urls.add("https://www." + site);
            urls.add("http://" + site);
            urls.add("http://www." + site);
        }

        return urls;
    }
}
