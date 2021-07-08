package com.zjh.flashsale.db.dao;

import com.zjh.flashsale.db.mappers.FlashsaleActivityMapper;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Repository
public class FlashsaleActivityDaoImpl implements FlashsaleActivityDao {

    @Resource
    private FlashsaleActivityMapper flashsaleActivityMapper;

    @Override
    public List<FlashsaleActivity> queryFlashsaleActivitysByStatus(int activityStatus) {
        return flashsaleActivityMapper.queryFlashsaleActivitysByStatus(activityStatus);
    }

    @Override
    public void insertFlashsaleActivity(FlashsaleActivity flashsaleActivity) {
        flashsaleActivityMapper.insert(flashsaleActivity);
    }

    @Override
    public FlashsaleActivity queryFlashsaleActivityById(long activityId) {
        return flashsaleActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateFlashsaleActivity(FlashsaleActivity flashsaleActivity) {
        flashsaleActivityMapper.updateByPrimaryKey(flashsaleActivity);
    }

    @Override
    public boolean deductStock(long activityId) {
        int result = flashsaleActivityMapper.deductStock(activityId);
        if (result < 1) {
            log.error("扣减库存失败");
            return false;
        }
        return true;
    }

    @Override
    public boolean lockStock(long activityId) {
        int result = flashsaleActivityMapper.lockStock(activityId);
        if (result < 1) {
            log.error("锁定库存失败");
            return false;
        }
        return true;
    }
}
