package com.accountooze.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "lead")
public class Lead extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "firstname",columnDefinition = "text")
    private String firstName;

    @Column(name = "lastname",columnDefinition = "text")
    private String lastName;

    @Column(name = "email",columnDefinition = "text")
    private String email;

    @Column(name = "country",columnDefinition = "text")
    private String country;

    @Column(name = "industry",columnDefinition = "text")
    private String industry;

    @Column(name = "phone",columnDefinition = "text")
    private String phone;

    @Column(name = "companyname",columnDefinition = "text")
    private String companyName;

    @Column(name = "verifiedstatus",columnDefinition = "text")
    private String verifiedStatus;

    @Column(name = "verifiedon",columnDefinition = "text")
    private String verifiedOn;

    @Column(name = "campaignid",columnDefinition = "text")
    private String campaignId;

    @Column(name = "campaignofinstantly",columnDefinition = "text")
    private String campaignOfInstantly;

    @Column(name = "title",columnDefinition = "text")
    private String title;

    @Column(name = "website",columnDefinition = "text")
    private String website;

    @Column(name = "leadstatus",columnDefinition = "text")
    private String leadstatus;

    @Column(name = "userid")
    private Long userId;


}
