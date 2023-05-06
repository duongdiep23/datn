package com.dshop.dshop.utils;

import org.springframework.stereotype.Component;

@Component
public class TimeUtils {
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
