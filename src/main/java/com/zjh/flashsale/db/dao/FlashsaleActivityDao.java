package com.zjh.flashsale.db.dao;

import com.zjh.flashsale.db.po.FlashsaleActivity;

import java.util.List;

public interface FlashsaleActivityDao {

    public List<FlashsaleActivity> queryFlashsaleActivitysByStatus(int activityStatus);

    public void insertFlashsaleActivity(FlashsaleActivity flashsaleActivity);

    public FlashsaleActivity queryFlashsaleActivityById(long activityId);

    public void updateFlashsaleActivity(FlashsaleActivity flashsaleActivity);
}
