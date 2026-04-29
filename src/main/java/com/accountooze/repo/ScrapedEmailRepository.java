package com.accountooze.repo;

import com.accountooze.model.ScrapedEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapedEmailRepository  extends JpaRepository<ScrapedEmail, Integer> {
    boolean existsByEmailAndWebsite(String email, String website);
}
