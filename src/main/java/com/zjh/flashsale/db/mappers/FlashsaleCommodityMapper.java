package com.zjh.flashsale.db.mappers;

import com.zjh.flashsale.db.po.FlashsaleCommodity;

public interface FlashsaleCommodityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FlashsaleCommodity record);

    int insertSelective(FlashsaleCommodity record);

    FlashsaleCommodity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FlashsaleCommodity record);

    int updateByPrimaryKey(FlashsaleCommodity record);
}