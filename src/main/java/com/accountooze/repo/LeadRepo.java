package com.accountooze.repo;

import com.accountooze.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LeadRepo extends JpaRepository<Lead, Integer> {
    Page<Lead> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT l.email FROM lead l WHERE l.email IS NOT NULL AND l.userId = ?1")
    List<String> findAllEmails(Long userId);

    List<Lead> findByIdInAndUserId(List<Integer> ids, Long userId);

    @Query("""
                SELECT l FROM lead l
                WHERE l.userId = :userId
    
                AND (:firstName IS NULL OR :firstName = '' OR LOWER(l.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')))
                AND (:lastName IS NULL OR :lastName = '' OR LOWER(l.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')))
                AND (:email IS NULL OR :email = '' OR LOWER(l.email) LIKE LOWER(CONCAT('%', :email, '%')))
                AND (:phone IS NULL OR :phone = '' OR LOWER(l.phone) LIKE LOWER(CONCAT('%', :phone, '%')))
                AND (:companyName IS NULL OR :companyName = '' OR LOWER(l.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))
                AND (:website IS NULL OR :website = '' OR LOWER(l.website) LIKE LOWER(CONCAT('%', :website, '%')))
                AND (:country IS NULL OR :country = '' OR LOWER(l.country) LIKE LOWER(CONCAT('%', :country, '%')))
                AND (:title IS NULL OR :title = '' OR LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%')))
                AND (:industry IS NULL OR :industry = '' OR LOWER(l.industry) LIKE LOWER(CONCAT('%', :industry, '%')))
                AND (:leadstatus IS NULL OR :leadstatus = '' OR LOWER(l.leadstatus) LIKE LOWER(CONCAT('%', :leadstatus, '%')))
                AND (:verifiedStatus IS NULL OR :verifiedStatus = '' OR LOWER(l.verifiedStatus) LIKE LOWER(CONCAT('%', :verifiedStatus, '%')))
                 AND (:verifiedOn IS NULL OR :verifiedOn = '' OR LOWER(l.verifiedOn) LIKE LOWER(CONCAT('%', :verifiedOn, '%')))
                AND (:campaignId IS NULL OR :campaignId = '' OR LOWER(l.campaignId) LIKE LOWER(CONCAT('%', :campaignId, '%')))
                AND (:campaignOfInstantly IS NULL OR :campaignOfInstantly = '' OR LOWER(l.campaignOfInstantly) LIKE LOWER(CONCAT('%', :campaignOfInstantly, '%')))
                ORDER BY l.id DESC
            """)
    Page<Lead> findLeadsWithFilters(@Param("userId") Long userId, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String email, @Param("phone") String phone, @Param("companyName") String companyName, @Param("website") String website, @Param("country") String country, @Param("industry") String industry, @Param("leadstatus") String leadstatus, @Param("verifiedStatus") String verifiedStatus, @Param("verifiedOn") String verifiedOn, @Param("campaignId") String campaignId, @Param("campaignOfInstantly") String campaignOfInstantly,@Param("title") String title, Pageable pageable);

    Lead findByEmailAndUserId(String email, Long loginUserId);
}
