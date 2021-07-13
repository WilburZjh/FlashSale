package com.zjh.flashsale.db.mappers;

import com.zjh.flashsale.db.po.FlashsaleActivity;

import java.util.List;

public interface FlashsaleActivityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FlashsaleActivity record);

    int insertSelective(FlashsaleActivity record);

    FlashsaleActivity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashsaleActivity record);

    int updateByPrimaryKey(FlashsaleActivity record);

    List<FlashsaleActivity> queryFlashsaleActivitysByStatus(int activityStatus);

    int lockStock(Long id);

    int deductStock(Long id);

    void revertStock(Long seckillActivityId);
}