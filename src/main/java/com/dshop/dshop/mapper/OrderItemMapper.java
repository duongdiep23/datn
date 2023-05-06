package com.dshop.dshop.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(uses = SizeMapper.class)
public interface OrderItemMapper {
}
