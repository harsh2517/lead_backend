package com.accountooze.repo;

import com.accountooze.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeadRepo extends JpaRepository<Lead, Integer> {
    List<Lead> findByUserId(Long loginUserId);

    @Query("SELECT l.email FROM lead l WHERE l.email IS NOT NULL")
    List<String> findAllEmails();
}
