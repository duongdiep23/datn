package com.dshop.dshop.models.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    private String password;
}
