package com.accountooze.service;

import com.accountooze.model.Lead;
import com.accountooze.repo.LeadRepo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeadService {

    @Autowired
    private LeadRepo leadRepo;

    private String normalizeHeader(String s) {
        return s == null ? "" : s.replace("\uFEFF", "").trim().toLowerCase();
    }

    private String getValue(CSVRecord row, Map<String, Integer> headerMap, String columnName) {

        if (columnName == null || columnName.isBlank()) return null;

        Integer index = headerMap.get(normalizeHeader(columnName));

        if (index == null) return null;

        try {
            String value = row.get(index);

            if (value == null) return null;

            value = value.trim();

            if (value.isEmpty()
                    || value.equalsIgnoreCase("null")
                    || value.equalsIgnoreCase("na")
                    || value.equalsIgnoreCase("n/a")
                    || value.equals("-")) {
                return null;
            }

            return value;

        } catch (Exception e) {
            return null;
        }
    }

    public List<Lead> parseFileToLead(
            MultipartFile file,
            List<String> columns,
            Long loginUserId
    ) throws IOException {

        if (columns == null || columns.size() < 14) {
            throw new RuntimeException("Please map all required columns");
        }

        List<Lead> list = new ArrayList<>();

        List<String> existingEmails = leadRepo.findAllEmails();
        Set<String> existingEmailSet = existingEmails.stream()
                .filter(e -> e != null)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Set<String> csvEmailSet = new HashSet<>();

        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser parser = CSVFormat.DEFAULT
                        .builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreSurroundingSpaces(true)
                        .setTrim(true)
                        .setQuote('"')
                        .build()
                        .parse(reader)
        ) {

            Map<String, Integer> headerMap = parser.getHeaderMap()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            e -> normalizeHeader(e.getKey()),
                            Map.Entry::getValue,
                            (a, b) -> a
                    ));

            for (CSVRecord row : parser) {

                String email = getValue(row, headerMap, columns.get(2));


                if (email == null) continue;

                email = email.toLowerCase();

                if (existingEmailSet.contains(email)) continue;

                if (csvEmailSet.contains(email)) continue;

                csvEmailSet.add(email);
                Lead lead = new Lead();

                lead.setFirstName(getValue(row, headerMap, columns.get(0)));
                lead.setLastName(getValue(row, headerMap, columns.get(1)));
                lead.setEmail(email);
                lead.setCountry(getValue(row, headerMap, columns.get(3)));
                lead.setIndustry(getValue(row, headerMap, columns.get(4)));
                lead.setPhone(getValue(row, headerMap, columns.get(5)));
                lead.setCompanyName(getValue(row, headerMap, columns.get(6)));
                lead.setVerifiedStatus(getValue(row, headerMap, columns.get(7)));
                lead.setVerifiedOn(getValue(row, headerMap, columns.get(8)));
                lead.setCampaignId(getValue(row, headerMap, columns.get(9)));
                lead.setCampaignOfInstantly(getValue(row, headerMap, columns.get(10)));
                lead.setTitle(getValue(row, headerMap, columns.get(11)));
                lead.setWebsite(getValue(row, headerMap, columns.get(12)));
                lead.setLeadstatus(getValue(row, headerMap, columns.get(13)));

                lead.setUserId(loginUserId);

                list.add(lead);
            }
        }

        return list;
    }

    public void save(List<Lead> reqList, Long loginUserId) {

        for (Lead lead : reqList) {
            lead.setUserId(loginUserId);
        }

        leadRepo.saveAll(reqList);
    }

    public List<Lead> getLead(Long loginUserId) {
    return leadRepo.findByUserId(loginUserId);
    }
}
