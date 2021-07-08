package com.zjh.flashsale.services;

import com.alibaba.fastjson.JSON;
import com.zjh.flashsale.db.dao.FlashsaleActivityDao;
import com.zjh.flashsale.db.dao.OrderDao;
import com.zjh.flashsale.db.po.FlashsaleActivity;
import com.zjh.flashsale.db.po.Order;
import com.zjh.flashsale.mq.RocketMQService;
import com.zjh.flashsale.util.RedisService;
import com.zjh.flashsale.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class FlashsaleActivityService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    private RocketMQService rocketMQService;
    @Autowired
    private OrderDao orderDao;

    /**
     * datacenterId;  数据中心
     * machineId;     机器标识
     * 在分布式环境中可以从机器配置上读取
     * 单机开发环境中先写死
     */
    private SnowFlake snowFlake = new SnowFlake(1, 1);

    /**
     * 判断秒杀库存
     *
     * @param activityId
     * @return
     */
    public boolean flashsaleStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return redisService.stockDeductValidator(key);
    }

    /**
     * 创建订单
     *
     * @param flashsaleActivityId
     * @param userId
     * @return
     * @throws Exception
     */
    public Order createOrder(long flashsaleActivityId, long userId) throws Exception {
        /*
         * 1.创建订单
         */
        FlashsaleActivity flashsaleActivity = flashsaleActivityDao.queryFlashsaleActivityById(flashsaleActivityId);
        Order order = new Order();
        //采用雪花算法生成订单ID
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setFlashsaleActivityId(flashsaleActivity.getId());
        order.setUserId(userId);
        order.setOrderAmount(flashsaleActivity.getFlashsalePrice().longValue());
        /*
         *2.发送创建订单消息
         */
        rocketMQService.sendMessage("flashsale_order", JSON.toJSONString(order));

        return order;
    }

    /**
     * 订单支付完成处理
     * @param orderNo
     */
    public void payOrderProcess(String orderNo) {
        log.info("完成支付订单 订单号：" + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        boolean deductStockResult = flashsaleActivityDao.deductStock(order.getFlashsaleActivityId());
        if (deductStockResult) {
            order.setPayTime(new Date());
            // 订单状态 0、没有可用库存，无效订单  1、已创建等待支付  2、完成支付
            order.setOrderStatus(2);
            orderDao.updateOrder(order);
        }
    }

}