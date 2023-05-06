package com.dshop.dshop.models.dtos;

import com.dshop.dshop.models.Size;
import lombok.Data;

import java.util.List;

@Data
public class ColorDTO {
    private String value;

    private List<SizeDTO> sizes;
}
