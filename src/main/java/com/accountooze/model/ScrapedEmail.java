package com.accountooze.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "scrapedemail")
public class ScrapedEmail extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String website;

    private String email;

    private int userId;
}
