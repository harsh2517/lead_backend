package com.accountooze.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "usermaster")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;
}
