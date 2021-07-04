package com.zjh.flashsale.db.dao;

import com.zjh.flashsale.db.mappers.FlashsaleCommodityMapper;
import com.zjh.flashsale.db.po.FlashsaleCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class FlashsaleCommodityDaoImpl implements FlashsaleCommodityDao {

    @Resource
    private FlashsaleCommodityMapper flashsaleCommodityMapper;

    @Override
    public FlashsaleCommodity queryFlashsaleCommodityById(long commodityId) {
        return flashsaleCommodityMapper.selectByPrimaryKey(commodityId);
    }
}
