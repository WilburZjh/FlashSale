
package com.zjh.flashsale;

import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.db.mappers.FlashsaleActivityMapper;
import com.zjh.flashsale.db.mappers.FlashsaleCommodityMapper;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import com.zjh.flashsale.db.po.FlashsaleCommodity;
import com.zjh.flashsale.services.FlashsaleOverSellService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DaoTest {

    @Resource
    private FlashsaleActivityMapper flashsaleActivityMapper;

    @Resource
    private FlashsaleCommodityMapper flashsaleCommodityMapper;


    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private FlashsaleOverSellService flashsaleOverSellService;



    @Test
    void FlashsaleActivityTest() {
        FlashsaleActivity flashsaleActivity = new FlashsaleActivity();
        flashsaleActivity.setName("测试");
        flashsaleActivity.setCommodityId(999L);
        flashsaleActivity.setTotalStock(100L);
        flashsaleActivity.setFlashsalePrice(new BigDecimal(99));
        flashsaleActivity.setOldPrice(new BigDecimal(299));
        flashsaleActivity.setAvailableStock(50);
        flashsaleActivity.setActivityStatus(16);
        flashsaleActivity.setLockStock(90L);
        flashsaleActivityMapper.insert(flashsaleActivity);
        System.out.println("====>>>>" + flashsaleActivityMapper.selectByPrimaryKey(1L));
    }


    @Test
    void FlashsaleCommodityTest() {
        FlashsaleCommodity flashsaleCommodity = new FlashsaleCommodity();
        flashsaleCommodity.setCommodityName("11");
        flashsaleCommodity.setCommodityDesc("11");
        flashsaleCommodity.setCommodityPrice(100);
        flashsaleCommodityMapper.insert(flashsaleCommodity);
        flashsaleCommodityMapper.selectByPrimaryKey(11L);
        System.out.println("====>>>>" + flashsaleCommodityMapper.selectByPrimaryKey(1L));
    }

    @Test
    void setFlashsaleActivityQuery(){
        List<FlashsaleActivity> flashsaleActivitys = flashsaleActivityDao.queryFlashsaleActivitysByStatus(0);
        System.out.println(flashsaleActivitys.size());
        flashsaleActivitys.stream().forEach(flashsaleActivity -> System.out.println(flashsaleActivity.toString()));
    }

    @Test
    void overSellServiceTest(){
        for(int i=0;i<1000;i++){
            flashsaleOverSellService.processFlashsale(11L);
        }
    }
}
