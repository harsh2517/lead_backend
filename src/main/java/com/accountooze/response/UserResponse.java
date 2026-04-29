package com.accountooze.response;

import com.accountooze.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {

    private int id;
    private String name;
    private String accessToken;
    private String refreshToken;
    private String email;
    private Date expires_in;


    public static UserResponse getUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setId(user.getId());
        return userResponse;
    }

}
