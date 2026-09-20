package com.edu.analysis.dto;

import lombok.Data;

/**
 * 登录请求参数
 */
@Data
public class LoginDTO {
    private String username;
    private String password;
}
