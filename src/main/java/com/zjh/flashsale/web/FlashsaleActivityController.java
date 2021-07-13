package com.zjh.flashsale.web;

import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.db.dao.FlashsaleCommodityDao;
import com.zjh.flashsale.db.dao.OrderDao;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import com.zjh.flashsale.db.po.FlashsaleCommodity;
import com.zjh.flashsale.db.po.Order;
import com.zjh.flashsale.services.FlashsaleActivityService;
import com.zjh.flashsale.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class FlashsaleActivityController {
    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private FlashsaleCommodityDao flashsaleCommodityDao;

    @Autowired
    private FlashsaleActivityService flashsaleActivityService;

    @Autowired
    private OrderDao orderDao;

    @Resource
    private RedisService redisService;

    /**
     * 查询秒杀活动的列表
     *
     * @param resultMap
     * @return
     */
    @RequestMapping("/flashsales")
    public String sucessTest(Map<String, Object> resultMap) {
        List<FlashsaleActivity> flashsaleActivities = flashsaleActivityDao.queryFlashsaleActivitysByStatus(1);
//        resultMap.put("flashsaleActivities", flashsaleActivities);
//        return "flashsale_activity";
        for (FlashsaleActivity flashsaleActivity : flashsaleActivities) {
            redisService.setValue("stock:" + flashsaleActivity.getId(),
                    (long) flashsaleActivity.getAvailableStock());

            System.out.println(flashsaleActivity.getAvailableStock());
        }
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

    /**
     * 处理抢购请求
     * @param userId
     * @param flashsaleActivityId
     * @return
     */
    @ResponseBody
    @RequestMapping("/flashsale/buy/{userId}/{flashsaleActivityId}")
    public ModelAndView flashsaleCommodity(@PathVariable long userId, @PathVariable long flashsaleActivityId) {
        boolean stockValidateResult = false;

        ModelAndView modelAndView = new ModelAndView();
        try {
            /*
             * 判断用户是否在已购名单中
             */
            if (redisService.isInLimitMember(flashsaleActivityId, userId)) {
                //提示用户已经在限购名单中，返回结果
                modelAndView.addObject("resultInfo", "对不起，您已经在限购名单中");
                modelAndView.setViewName("seckill_result");
                return modelAndView;
            }
            /*
             * 确认是否能够进行秒杀
             */
            stockValidateResult = flashsaleActivityService.flashsaleStockValidator(flashsaleActivityId);
            if (stockValidateResult) {
                Order order = flashsaleActivityService.createOrder(flashsaleActivityId, userId);
                modelAndView.addObject("resultInfo","秒杀成功，订单创建中，订单ID：" + order.getOrderNo());
                modelAndView.addObject("orderNo",order.getOrderNo());
            } else {
                modelAndView.addObject("resultInfo","对不起，商品库存不足");
            }
        } catch (Exception e) {
            log.error("秒杀系统异常" + e.toString());
            modelAndView.addObject("resultInfo","秒杀失败");
        }
        modelAndView.setViewName("flashsale_result");
        return modelAndView;
    }

    /**
     * 订单查询
     * @param orderNo
     * @return
     */
    @RequestMapping("/flashsale/orderQuery/{orderNo}")
    public ModelAndView orderQuery(@PathVariable String orderNo) {
        log.info("订单查询，订单号：" + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        ModelAndView modelAndView = new ModelAndView();

        if (order != null) {
            modelAndView.setViewName("order");
            modelAndView.addObject("order", order);
            FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(order.getFlashsaleActivityId());
            modelAndView.addObject("flashsaleActivity", flashsaleActivity);
        } else {
            modelAndView.setViewName("order_wait");
        }
        return modelAndView;
    }

    /**
     * 订单支付
     * @return
     */
    @RequestMapping("/flashsale/payOrder/{orderNo}")
    public String payOrder(@PathVariable String orderNo) throws Exception{
        flashsaleActivityService.payOrderProcess(orderNo);
        return "redirect:/flashsale/orderQuery/" + orderNo;
    }
}
