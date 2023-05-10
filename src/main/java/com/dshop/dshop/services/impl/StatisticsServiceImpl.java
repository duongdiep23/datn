package com.dshop.dshop.services.impl;


import com.dshop.dshop.libraries.Utilities;
import com.dshop.dshop.repositories.OrderDetailRepository;
import com.dshop.dshop.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final int TYPE_ORDER = 1;
    private final OrderDetailRepository orderDetailRepository;

    private final Utilities utilities;

    @Autowired
    public StatisticsServiceImpl(OrderDetailRepository orderDetailRepository, Utilities utilities) {
        this.orderDetailRepository = orderDetailRepository;
        this.utilities = utilities;
    }

    @Override
    public long getOrderByDate(Date date) {
        try{
            String strDate = utilities.formatToStrDate(date);
            return orderDetailRepository.countByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public long getOrderByMonth(Date date) {
        try {
            String strDate =utilities.formatToStrMonth(date);
            return orderDetailRepository.countByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getOrderByYear(Date date) {
        try {
            String strDate =utilities.formatToStrYear(date);
            return orderDetailRepository.countByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getRevenueByDate(Date date) {
        try{
            String strDate =utilities.formatToStrDate(date);
            return orderDetailRepository.revenueByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public long getRevenueByMonth(Date date) {
        try {
            String strDate =utilities.formatToStrMonth(date);
            return orderDetailRepository.revenueByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public long getRevenueByYear(Date date) {
        try {
            String strDate =utilities.formatToStrYear(date);
            return orderDetailRepository.revenueByCreatedDateAndType(strDate, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public long getOrderByStatus(int status, Date date) {
        try {
            String strDate =utilities.formatToStrDate(date);
            return orderDetailRepository.countByCreatedDateAndStatusAndType(strDate, status, TYPE_ORDER);
        }catch (Exception e){
            return 0;
        }
    }
}
