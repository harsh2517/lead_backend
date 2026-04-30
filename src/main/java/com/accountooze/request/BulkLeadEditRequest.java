package com.accountooze.request;

import lombok.Data;

import java.util.List;
@Data
public class BulkLeadEditRequest {
    private List<Integer> ids;

    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String industry;
    private String phone;
    private String companyName;
    private String verifiedStatus;
    private String verifiedOn;
    private String campaignId;
    private String campaignOfInstantly;
    private String title;
    private String website;
    private String leadstatus;
}
