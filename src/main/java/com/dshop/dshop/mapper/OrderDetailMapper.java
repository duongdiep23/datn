package com.dshop.dshop.mapper;

import com.dshop.dshop.models.OrderDetail;
import com.dshop.dshop.models.request.UpdateOrderRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface OrderDetailMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(@MappingTarget OrderDetail orderDetail, UpdateOrderRequest updateOrderRequest);
}
