package com.accountooze.service;

import com.accountooze.config.JwtUtil;
import com.accountooze.exception.UserException;
import com.accountooze.model.LoginHistory;
import com.accountooze.model.User;
import com.accountooze.repo.LoginHistoryRepository;
import com.accountooze.repo.UserRepo;
import com.accountooze.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    public UserResponse createUser(String email, String password) {

        User existingUser = userRepo.findByEmail(email);
        if (existingUser != null) {
            throw new UserException("User already exists with this email: " + email);
        }

        User user = new User();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepo.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        Date date = jwtUtil.extractExpiration(accessToken);

        LoginHistory history = new LoginHistory();
        history.setAccessToken(accessToken);
        history.setRefreshToken(refreshToken);
        history.setUser(user);
        loginHistoryRepository.save(history);

        UserResponse userResponse = new UserResponse();
        userResponse = UserResponse.getUserResponse(user);
        userResponse.setRefreshToken(refreshToken);
        userResponse.setAccessToken(accessToken);
        userResponse.setExpires_in(date);

        return userResponse;
    }

    public UserResponse loginUser(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new UserException("email not found");


        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new UserException("password not match");

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        Date date = jwtUtil.extractExpiration(accessToken);

        LoginHistory history = new LoginHistory();
        history.setAccessToken(accessToken);
        history.setRefreshToken(refreshToken);
        history.setUser(user);
        loginHistoryRepository.save(history);

        UserResponse userResponse = new UserResponse();
        userResponse = UserResponse.getUserResponse(user);
        userResponse.setRefreshToken(refreshToken);
        userResponse.setAccessToken(accessToken);
        userResponse.setExpires_in(date);

        return userResponse;
    }
}
