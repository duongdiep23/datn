package com.dshop.dshop.models.request;

import lombok.Data;

import java.util.List;

@Data
public class ColorListRequest {
    List<ColorRequest> colorRequests;
}
