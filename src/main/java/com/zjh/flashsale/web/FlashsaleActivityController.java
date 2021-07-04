package com.zjh.flashsale.web;

import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.db.dao.FlashsaleCommodityDao;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import com.zjh.flashsale.db.po.FlashsaleCommodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Controller
public class FlashsaleActivityController {
    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private FlashsaleCommodityDao flashsaleCommodityDao;

    /**
     * 查询秒杀活动的列表
     *
     * @param resultMap
     * @return
     */
    @RequestMapping("/flashsales")
    public String sucessTest(Map<String, Object> resultMap) {
        List<FlashsaleActivity> flashsaleActivities = flashsaleActivityDao.queryFlashsaleActivitysByStatus(1);
        resultMap.put("flashsaleActivities", flashsaleActivities);
        return "flashsale_activity";
    }

    /**
     * 秒杀商品详情
     *
     * @param resultMap
     * @param flashsaleActivityId
     * @return
     */
    @RequestMapping("/item/{flashsaleActivityId}")
    public String itemPage(Map<String, Object> resultMap, @PathVariable long flashsaleActivityId) {
        FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(flashsaleActivityId);
        FlashsaleCommodity flashsaleCommodity = flashsaleCommodityDao.queryFlashsaleCommodityById(flashsaleActivity.getCommodityId());

        resultMap.put("flashsaleActivity", flashsaleActivity);
        resultMap.put("flashsaleCommodity", flashsaleCommodity);
        resultMap.put("flashsalePrice", flashsaleActivity.getFlashsalePrice());
        resultMap.put("oldPrice", flashsaleActivity.getOldPrice());
        resultMap.put("commodityId", flashsaleActivity.getCommodityId());
        resultMap.put("commodityName", flashsaleCommodity.getCommodityName());
        resultMap.put("commodityDesc", flashsaleCommodity.getCommodityDesc());
        return "flashsale_item";
    }

//    @ResponseBody
    @RequestMapping("/addFlashsaleActivityAction")
    public String addFlashsaleActivityAction(
            @RequestParam("name") String name,
            @RequestParam("commodityId") long commodityId,
            @RequestParam("flashsalePrice") BigDecimal flashsalePrice,
            @RequestParam("oldPrice") BigDecimal oldPrice,
            @RequestParam("flashsaleNumber") long flashsaleNumber,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            Map<String, Object> resultMap
    ) throws ParseException {
        startTime = startTime.substring(0, 10) +  startTime.substring(11);
        endTime = endTime.substring(0, 10) +  endTime.substring(11);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddhh:mm");
        FlashsaleActivity flashsaleActivity = new FlashsaleActivity();
        flashsaleActivity.setName(name);
        flashsaleActivity.setCommodityId(commodityId);
        flashsaleActivity.setFlashsalePrice(flashsalePrice);
        flashsaleActivity.setOldPrice(oldPrice);
        flashsaleActivity.setTotalStock(flashsaleNumber);
        flashsaleActivity.setAvailableStock(new Integer("" + flashsaleNumber));
        flashsaleActivity.setLockStock(0L);
        flashsaleActivity.setActivityStatus(1);
        flashsaleActivity.setStartTime(format.parse(startTime));
        flashsaleActivity.setEndTime(format.parse(endTime));
        flashsaleActivityDao.insertFlashsaleActivity(flashsaleActivity);
        resultMap.put("flashsaleActivity", flashsaleActivity);
        return "add_success";
    }

    @RequestMapping("/addFlashsaleActivity")
    public String addFlashsaleActivity() {
        return "add_activity";
    }
}
