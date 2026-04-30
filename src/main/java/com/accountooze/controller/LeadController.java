package com.accountooze.controller;


import com.accountooze.model.Lead;
import com.accountooze.request.BulkLeadDeleteRequest;
import com.accountooze.request.BulkLeadEditRequest;
import com.accountooze.request.LeadRequest;
import com.accountooze.response.ApiResponse;
import com.accountooze.service.LeadService;
import com.accountooze.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lead")
public class LeadController {

    @Autowired
    private LeadService leadService;

    @Autowired
    Utils utils;

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file, @RequestParam List<String> columns, HttpServletRequest request) {
        try {
            Long loginUserId = utils.getLoginUserId(request);

            List<Lead> reqList = leadService.parseFileToLead(file, columns, loginUserId);
            leadService.save(reqList, loginUserId);

            return ResponseEntity.ok("Imported leads: " + reqList.size());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/get")
    public ResponseEntity<Object> getLead(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,

                                          @RequestParam(required = false) String firstName,@RequestParam(required = false) String lastName,@RequestParam(required = false) String email,@RequestParam(required = false) String phone,@RequestParam(required = false) String companyName,@RequestParam(required = false) String website, @RequestParam(required = false) String country, @RequestParam(required = false) String industry, @RequestParam(required = false) String leadstatus, @RequestParam(required = false) String verifiedStatus,@RequestParam(required = false) String verifiedOn, @RequestParam(required = false) String campaignId, @RequestParam(required = false) String campaignOfInstantly,@RequestParam(required = false) String title) {
        return new ResponseEntity<>(new ApiResponse(leadService.getLead(utils.getLoginUserId(request), page, size,  firstName,lastName,email,phone,companyName,website, country, industry, leadstatus, verifiedStatus,verifiedOn, campaignId, campaignOfInstantly,title)), HttpStatus.OK);
    }

    @PutMapping("/bulk-edit")
    public ResponseEntity<?> bulkEdit(@RequestBody BulkLeadEditRequest bulkLeadEditRequest, HttpServletRequest request) {
        try {
            Long loginUserId = utils.getLoginUserId(request);
            return ResponseEntity.ok(new ApiResponse(leadService.bulkEdit(loginUserId, bulkLeadEditRequest)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> bulkDelete(@RequestBody BulkLeadDeleteRequest bulkLeadDeleteRequest, HttpServletRequest request) {
        try {
            Long loginUserId = utils.getLoginUserId(request);
            return ResponseEntity.ok(new ApiResponse(leadService.bulkDelete(loginUserId, bulkLeadDeleteRequest.getIds())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
