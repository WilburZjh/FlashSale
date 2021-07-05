package com.zjh.flashsale.services;

import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.util.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlashsaleActivityService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    public boolean flashsaleStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return redisService.stockDeductValidator(key);
    }
}