package com.accountooze.controller;

import com.accountooze.exception.UserAuthorizationException;
import com.accountooze.response.ApiResponse;
import com.accountooze.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/sign-up")
    public ResponseEntity<Object> createUser(@RequestParam String email, @RequestParam String password)
            throws UserAuthorizationException {
        return new ResponseEntity<>(new ApiResponse(userService.createUser(email, password)), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Object> loginUser(@RequestParam String email, @RequestParam String password)
            throws UserAuthorizationException {
        return new ResponseEntity<>(new ApiResponse(userService.loginUser(email, password)), HttpStatus.OK);
    }

}
