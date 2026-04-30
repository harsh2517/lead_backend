package com.accountooze.service;

import com.accountooze.exception.UserException;
import com.accountooze.model.Lead;
import com.accountooze.repo.LeadRepo;
import com.accountooze.request.BulkLeadEditRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

            if (value.isEmpty() || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("na") || value.equalsIgnoreCase("n/a") || value.equals("-")) {
                return null;
            }

            return value;

        } catch (Exception e) {
            return null;
        }
    }

    public List<Lead> parseFileToLead(MultipartFile file, List<String> columns, Long loginUserId) throws IOException {

        if (columns == null || columns.size() < 14) {
            throw new RuntimeException("Please map all required columns");
        }

        List<Lead> list = new ArrayList<>();

        List<String> existingEmails = leadRepo.findAllEmails(loginUserId);
        Set<String> existingEmailSet = existingEmails.stream().filter(e -> e != null).map(String::toLowerCase).collect(Collectors.toSet());
        Set<String> csvEmailSet = new HashSet<>();

        try (Reader reader = new InputStreamReader(file.getInputStream()); CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreSurroundingSpaces(true).setTrim(true).setQuote('"').build().parse(reader)) {

            Map<String, Integer> headerMap = parser.getHeaderMap().entrySet().stream().collect(Collectors.toMap(e -> normalizeHeader(e.getKey()), Map.Entry::getValue, (a, b) -> a));

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

    public Page<Lead> getLead(Long loginUserId, int page, int size, String firstName,String lastName,String email,String phone,String companyName,String website, String country, String industry, String leadstatus, String verifiedStatus,String verifiedOn, String campaignId, String campaignOfInstantly,String title) {
        Pageable pageable = PageRequest.of(page, size);

        return leadRepo.findLeadsWithFilters(loginUserId, firstName,lastName,email,phone,companyName,website ,country, industry, leadstatus, verifiedStatus,verifiedOn, campaignId, campaignOfInstantly,title, pageable);
    }

    @Transactional
    public String bulkDelete(Long loginUserId, List<Integer> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Please select leads");
        }

        List<Lead> leads = leadRepo.findByIdInAndUserId(ids, loginUserId);

        if (leads.isEmpty()) {
            throw new RuntimeException("No leads found");
        }

        leadRepo.deleteAll(leads);

        return "Deleted leads: " + leads.size();
    }

    @Transactional
    public String bulkEdit(Long loginUserId, BulkLeadEditRequest request) {

        if (request.getIds() == null || request.getIds().isEmpty()) {
            throw new RuntimeException("Please select leads");
        }

        List<Lead> leads = leadRepo.findByIdInAndUserId(request.getIds(), loginUserId);

        if (leads.isEmpty()) {
            throw new RuntimeException("No leads found");
        }

        for (Lead lead : leads) {

            if (request.getFirstName() != null) {
                lead.setFirstName(request.getFirstName());
            }

            if (request.getLastName() != null) {
                lead.setLastName(request.getLastName());
            }

            if (request.getEmail() != null) {

                Lead emailAndUserId = leadRepo.findByEmailAndUserId(request.getEmail(), loginUserId);
                if (emailAndUserId != null) {
                    throw new UserException("Email already exist :- " + request.getEmail());
                }
                lead.setEmail(request.getEmail());
            }

            if (request.getCountry() != null) {
                lead.setCountry(request.getCountry());
            }

            if (request.getIndustry() != null) {
                lead.setIndustry(request.getIndustry());
            }

            if (request.getPhone() != null) {
                lead.setPhone(request.getPhone());
            }

            if (request.getCompanyName() != null) {
                lead.setCompanyName(request.getCompanyName());
            }

            if (request.getVerifiedStatus() != null) {
                lead.setVerifiedStatus(request.getVerifiedStatus());
            }

            if (request.getVerifiedOn() != null) {
                lead.setVerifiedOn(request.getVerifiedOn());
            }

            if (request.getCampaignId() != null) {
                lead.setCampaignId(request.getCampaignId());
            }

            if (request.getCampaignOfInstantly() != null) {
                lead.setCampaignOfInstantly(request.getCampaignOfInstantly());
            }

            if (request.getTitle() != null) {
                lead.setTitle(request.getTitle());
            }

            if (request.getWebsite() != null) {
                lead.setWebsite(request.getWebsite());
            }

            if (request.getLeadstatus() != null) {
                lead.setLeadstatus(request.getLeadstatus());
            }
        }

        leadRepo.saveAll(leads);
        return "Updated leads: " + leads.size();
    }
}
