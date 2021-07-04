package com.zjh.flashsale.db.dao;

import com.zjh.flashsale.db.mappers.FlashsaleActivityMapper;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

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
}
