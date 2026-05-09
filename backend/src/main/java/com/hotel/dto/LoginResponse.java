package com.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String username;
    private String nickname;
    private String message;

    public LoginResponse(String token, String username, String nickname) {
        this.token = token;
        this.username = username;
        this.nickname = nickname;
        this.message = "登录成功";
    }
}
