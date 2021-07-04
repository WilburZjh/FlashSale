package com.zjh.flashsale.services;

import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlashsaleOverSellService {
    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    public String processFlashsale(long activityId) {
        FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(activityId);
        long availableStock = flashsaleActivity.getAvailableStock();
        String result;
        if (availableStock > 0) {
            result = "恭喜，抢购成功";
            System.out.println(result);
            availableStock = availableStock - 1;
            flashsaleActivity.setAvailableStock(new Integer("" + availableStock));
            flashsaleActivityDao.updateFlashsaleActivity(flashsaleActivity);
        } else {
            result = "抱歉，抢购失败，商品被抢完了";
            System.out.println(result);
        }
        return result;
    }
}
