package com.dshop.dshop.models.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;

    private String password;

    private String fullname;

    private String email;
}
