package com.dshop.dshop.models.request;

import lombok.Data;

@Data
public class SizeRequest {
    private String value;

    private int total;

    private Long colorId;
}
