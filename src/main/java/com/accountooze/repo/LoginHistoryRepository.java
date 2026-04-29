package com.accountooze.repo;

import com.accountooze.model.LoginHistory;
import com.accountooze.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByAccessToken(String accessToken, Pageable page);

    boolean existsByUser(User user);
}
